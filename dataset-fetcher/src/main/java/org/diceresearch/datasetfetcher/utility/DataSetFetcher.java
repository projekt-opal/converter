package org.diceresearch.datasetfetcher.utility;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.diceresearch.datasetfetcher.messaging.SourceWithDynamicDestination;
import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.model.WorkingStatus;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class DataSetFetcher implements Runnable {

    private final PortalRepository portalRepository;
    private final QueryExecutionFactoryHttpProvider queryExecutionFactoryHttpProvider;
    private final SourceWithDynamicDestination sourceWithDynamicDestination;

    private Resource portalResource;
    private Integer portalId;


    @Autowired
    public DataSetFetcher(PortalRepository portalRepository, QueryExecutionFactoryHttpProvider queryExecutionFactoryHttpProvider, SourceWithDynamicDestination sourceWithDynamicDestination) {
        this.portalRepository = portalRepository;
        this.queryExecutionFactoryHttpProvider = queryExecutionFactoryHttpProvider;
        this.sourceWithDynamicDestination = sourceWithDynamicDestination;
    }

    public void initialQueryExecutionFactory(Integer id) {
        this.portalId = id;
        Optional<Portal> optionalPortal = this.portalRepository.findById(id);
        if (!optionalPortal.isPresent()) return;
        this.queryExecutionFactoryHttpProvider.initialQueryExecutionFactory(id);

        Portal portal = optionalPortal.get();
        portalRepository.save(portal);
        portalResource = ResourceFactory.createResource("http://projekt-opal.de/catalog/" + portal.getName());
    }

    @Override
    public void run() {
        try {
            initializeFetching();
            if (doFetching()) return;
            terminateFetching();
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    private boolean doFetching() {
        Portal portal = portalRepository.findById(portalId).get();
        log.trace("doFetching {}", StructuredArguments.kv("portal", portal));
        for (int idx = portal.getLastNotFetched(); idx < portal.getHigh(); idx += portal.getStep()) {
            portal = portalRepository.findById(portalId).get();
            updateLastNotFetched(portal, idx);
            if (checkPaused(portal)) return true;
            fetchAndPublishDataSets(portal, idx);
        }
        return false;
    }

    private void fetchAndPublishDataSets(Portal portal, int idx) {
        log.trace("called: fetchAndPublishDataSets, {}, {}", StructuredArguments.kv("idx", idx), StructuredArguments.kv("portal", portal));
        String outputQueue = portal.getOutputQueue();
        List<Resource> listOfDataSets = getListOfDataSets(portal, idx);
        fetchAndPublishGraphOfDataSets(outputQueue, listOfDataSets);
    }

    private void fetchAndPublishGraphOfDataSets(String outputQueue, List<Resource> listOfDataSets) {
        log.trace("called: fetchAndPublishGraphOfDataSets, {}, {}", StructuredArguments.kv("outputQueue", outputQueue),
                StructuredArguments.kv("listOfDataSets.size()", listOfDataSets.size()));

        listOfDataSets.parallelStream().forEach(resource -> {
            Model graph = getGraph(resource);
            logUri(graph);
            byte[] serialize = ModelSerialization.serialize(graph);

            log.trace("publish serialized graph, {}, {}", StructuredArguments.kv("outputQueue", outputQueue),
                    StructuredArguments.kv("resource", resource));
            sourceWithDynamicDestination.sendMessage(serialize, outputQueue);
        });
    }

    private List<Resource> getListOfDataSets(Portal portal, int idx) {
        log.trace("called: getListOfDataSets, {}, {}", StructuredArguments.kv("idx", idx), StructuredArguments.kv("portal", portal));
        int limit = Math.min(portal.getStep(), portal.getHigh() - idx);
        return getListOfDataSets(idx, limit);
    }

    private boolean checkPaused(Portal portal) {
        if (portal.getWorkingStatus().equals(WorkingStatus.PAUSED)) {
            log.info("Paused fetching portal {}", portal);
            return true;
        }
        return false;
    }

    private void updateLastNotFetched(Portal portal, int idx) {
        log.trace("called: updateLastNotFetched, {}, {}", StructuredArguments.kv("idx", idx), StructuredArguments.kv("portal", portal));
        portal.setLastNotFetched(idx);
        portalRepository.save(portal);
    }

    private void terminateFetching() {
        log.trace("called: terminateFetching");
        Portal portal = portalRepository.findById(portalId).get();
        portal.setLastNotFetched(portal.getHigh());
        portal.setWorkingStatus(WorkingStatus.DONE);
        portalRepository.save(portal);
        log.info("Finished fetching portal {}", portal);
    }

    private void initializeFetching() throws Exception {
        Portal portal = portalRepository.findById(portalId).get();
        log.info("Start fetching {}", StructuredArguments.kv("portal", portal.getName()));

        int totalNumberOfDataSets = getTotalNumberOfDataSets();
        if (totalNumberOfDataSets == -1) throw new Exception("Cannot Query the TripleStore");

        tuningHigh(portal, totalNumberOfDataSets);
        portal.setWorkingStatus(WorkingStatus.RUNNING);
        portalRepository.save(portal);
        log.trace("initializeFetching, {}", StructuredArguments.kv("portal", portal));
    }

    private void tuningHigh(Portal portal, int totalNumberOfDataSets) {
        log.trace("tuningHigh, {}, {}", StructuredArguments.kv("portal.high", portal.getHigh()),
                StructuredArguments.kv("totalNumberOfDataSets", totalNumberOfDataSets));
        int high = portal.getHigh();
        if (high == -1 || high > totalNumberOfDataSets) high = totalNumberOfDataSets;
        portal.setHigh(high);
        log.trace("tuned high, {}", StructuredArguments.kv("portal.high", portal.getHigh()));
    }

    private void logUri(Model graph) {
        try {
            log.trace("called: logUri, {}", StructuredArguments.kv("graph.size()", graph.size()));
            ResIterator resIterator = graph.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource resource = resIterator.nextResource();
                log.info("{}", StructuredArguments.kv("originalUri", resource.getURI()));
            }
        } catch (Exception ignored) {
        }
    }

    private Model getGraph(Resource resource) {
        log.trace("called: getGraph, {}", StructuredArguments.kv("resource", resource));
        Model dataSetGraph = getAllPredicatesObjectsPublisherDistributions(resource);
        Resource catalog = getCatalog(resource);
        dataSetGraph.add(catalog, RDF.type, DCAT.Catalog);
        dataSetGraph.add(catalog, DCAT.dataset, resource);
        log.trace("return: getGraph, {}", StructuredArguments.kv("dataSetGraph", dataSetGraph.getGraph()));
        return dataSetGraph;
    }

    private Resource getCatalog(Resource dataSet) {
        log.trace("called: getCatalog, {}", StructuredArguments.kv("dataSet", dataSet));
        Resource catalog = null;
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                    "PREFIX dcat: <http://www.w3.org/ns/dcat#> " +
                    "PREFIX dct: <http://purl.org/dc/terms/> " +
                    "SELECT ?catalog " +
                    "WHERE { " +
                    "  GRAPH ?g { " +
                        "?catalog a dcat:Catalog ." +
                        "?catalog dcat:dataset ?dataSet . " +
                      "} " +
                    "}");
            pss.setParam("dataSet", dataSet);
            catalog = getCatalog(pss);
        } catch (Exception e) {
            log.error("Exception in getCatalog", e);
        }
        if (catalog == null) catalog = portalResource;
        log.trace("return: getCatalog, {}", StructuredArguments.kv("catalog", catalog.toString()));
        return catalog;
    }

    private Resource getCatalog(ParameterizedSparqlString pss) {
        log.trace("called: getCatalog, {}", StructuredArguments.kv("pss", pss.toString()));
        Resource catalog = null;
        try (QueryExecution queryExecution =
                     this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                catalog = solution.getResource("catalog");
                log.trace("get catalog: {}", catalog);
            }
        } catch (Exception ex) {
            log.error("Exception in getCatalog", ex);
        }
        log.trace("return: getCatalog, {}", StructuredArguments.kv("catalog", catalog != null ? catalog.toString() : null));
        return catalog;
    }

    private Model getAllPredicatesObjectsPublisherDistributions(Resource dataSet) {
        log.trace("called: getAllPredicatesObjectsPublisherDistributions, {}", StructuredArguments.kv("dataSet", dataSet));

        Model model;
        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "PREFIX dcat: <http://www.w3.org/ns/dcat#> " +
                "PREFIX dct: <http://purl.org/dc/terms/> " +
                "CONSTRUCT { " + "?dataSet ?predicate ?object . " +
                "?object ?p2 ?o2} " +
                "WHERE { " +
                "  GRAPH ?g { " +
                "    ?dataSet ?predicate ?object. " +
                "    OPTIONAL { ?object ?p2 ?o2 } " +
                "  } " +
                "}");

        pss.setParam("dataSet", dataSet);

        model = executeConstruct(pss);
        return model;
    }


    private Model executeConstruct(ParameterizedSparqlString pss) {
        log.trace("called: executeConstruct, {}", StructuredArguments.kv("pss", pss.toString()));
        Model model = null;
        try (QueryExecution queryExecution =
                     this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            model = queryExecution.execConstruct();
        } catch (Exception ex) {
            log.error("Exception in executing construct ", ex);
        }

        log.trace("return: executeConstruct, {}", StructuredArguments.kv("model.size()", model == null ? "null" : model.size()));
        return model;
    }

    /**
     * @return -1 => something went wrong, o.w. the number of distinct dataSets are return
     */
    private int getTotalNumberOfDataSets() {
        log.trace("called: getTotalNumberOfDataSets");
        int cnt;
        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "PREFIX dcat: <http://www.w3.org/ns/dcat#> " +
                "PREFIX dct: <http://purl.org/dc/terms/> " +
                "SELECT (COUNT(DISTINCT ?dataSet) AS ?num)\n" +
                "WHERE { \n" +
                "  GRAPH ?g {\n" +
                "    ?dataSet a dcat:Dataset.\n" +
                "    FILTER(EXISTS{?dataSet dct:title ?title.})\n" +
                "  }\n" +
                "}");


        cnt = getCount(pss);
        log.trace("return: {} from getTotalNumberOfDataSets", StructuredArguments.kv("cnt", cnt));
        return cnt;
    }


    private List<Resource> getListOfDataSets(int idx, int limit) {
        log.trace("called: getListOfDataSets, {}, {}", StructuredArguments.kv("idx", idx), StructuredArguments.kv("limit", limit));

        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "PREFIX dcat: <http://www.w3.org/ns/dcat#> " +
                "PREFIX dct: <http://purl.org/dc/terms/> " +
                "SELECT DISTINCT ?dataSet " +
                "WHERE {  " +
                "  GRAPH ?g { " +
                    " ?dataSet a dcat:Dataset. " +
                    " FILTER(EXISTS{?dataSet dct:title ?title.}) " +
                "  } " +
                "} " +
                "ORDER BY ?dataSet " +
                " OFFSET " + idx +
                " LIMIT " + limit
        );

        return getResources(pss);
    }

    private List<Resource> getResources(ParameterizedSparqlString pss) {
        log.trace("called: getResources, {}", StructuredArguments.kv("pss", pss.toString()));

        List<Resource> ret = new ArrayList<>();
        try (QueryExecution queryExecution = this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                Resource dataSet = solution.getResource("dataSet");
                ret.add(dataSet);
                log.trace("getResources get one, {}", StructuredArguments.kv("dataSet",dataSet));
            }
        } catch (Exception ex) {
            log.error("Exception in getting resources ", ex);
        }
        log.trace("return: getResources, {}", StructuredArguments.kv("ret.size()", ret.size()));
        return ret;
    }

    private int getCount(ParameterizedSparqlString pss) {
        log.trace("called: getCount, {}", StructuredArguments.kv("pss", pss.toString()));
        int cnt = -1;
        try (QueryExecution queryExecution =
                     this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                RDFNode num = solution.get("num");
                cnt = num.asLiteral().getInt();
            }
        } catch (Exception ex) {
            log.error("Exception in getting Count ", ex);
        }
        return cnt;
    }

}
