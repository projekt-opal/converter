package org.diceresearch.datasetfetcher.utility;

import com.google.common.collect.ImmutableMap;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.diceresearch.datasetfetcher.messaging.SourceWithDynamicDestination;
import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.model.WorkingStatus;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataSetFetcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataSetFetcher.class);

    private final PortalRepository portalRepository;
    private final QueryExecutionFactoryHttpProvider queryExecutionFactoryHttpProvider;
    private final SourceWithDynamicDestination sourceWithDynamicDestination;

    private Resource portalResource;
    private Integer portalId;

    private static final ImmutableMap<String, String> PREFIXES = ImmutableMap.<String, String>builder()
            .put("dcat", "http://www.w3.org/ns/dcat#")
            .put("dct", "http://purl.org/dc/terms/")
            .build();

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
            Optional<Portal> optionalPortal = portalRepository.findById(portalId);
            if (!optionalPortal.isPresent()) return;
            Portal portal = optionalPortal.get();
            logger.info("Start fetching {}", kv("portal", portal.getName()));

            int totalNumberOfDataSets = getTotalNumberOfDataSets();
            logger.debug("Total number of datasets is {}", kv("Total #Datasets", totalNumberOfDataSets));
            if (totalNumberOfDataSets == -1) throw new Exception("Cannot Query the TripleStore");

            int high = portal.getHigh();
            int lnf = portal.getLastNotFetched();
            if (high == -1 || high > totalNumberOfDataSets) high = totalNumberOfDataSets;
            portal.setHigh(high);
            Integer step = portal.getStep();
            portal.setWorkingStatus(WorkingStatus.RUNNING);
            portalRepository.save(portal);

            for (int idx = lnf; idx < high; idx += step) {
                optionalPortal = portalRepository.findById(portalId);
                if (!optionalPortal.isPresent()) return;
                Portal finalPortal = optionalPortal.get();
                if (finalPortal.getWorkingStatus().equals(WorkingStatus.PAUSED)) {
                    logger.info("Fetching portal {} is cancelled", finalPortal);
                    return;
                }
                finalPortal.setLastNotFetched(idx);
                portalRepository.save(finalPortal);

                int min = Math.min(step, high - idx);
                logger.info("Getting list dataSets  {} : {}", idx, idx + min);
                List<Resource> listOfDataSets = getListOfDataSets(idx, min);
                listOfDataSets
                        .parallelStream().forEach(resource -> {
                    Model graph = getGraph(resource);
                    byte[] serialize = RdfSerializerDeserializer.serialize(graph);
                    sourceWithDynamicDestination.sendMessage(serialize, finalPortal.getOutputQueue());
                });
            }
            optionalPortal = portalRepository.findById(portalId);
            if(optionalPortal.isPresent()) {
                portal = optionalPortal.get();
                portal.setLastNotFetched(high);
                portal.setWorkingStatus(WorkingStatus.DONE);
                portalRepository.save(portal);
            }
            logger.info("Fetching portal {} finished", portal);
        } catch (Exception e) {
            logger.error("Exception in convert", e);
        }
    }

    private Model getGraph(Resource resource) {
        Model dataSetGraph = getAllPredicatesObjectsPublisherDistributions(resource);
        Resource catalog = getCatalog(resource);
        if (catalog == null) catalog = portalResource;
        dataSetGraph.add(catalog, RDF.type, DCAT.Catalog);
        dataSetGraph.add(catalog, DCAT.dataset, resource);
        return dataSetGraph;
    }

    private Resource getCatalog(Resource dataSet) {
        Resource catalog = null;

        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                    "SELECT ?catalog " +
                    "WHERE { " +
                    "  GRAPH ?g { " +
                    "    ?catalog a dcat:Catalog ." +
                    "    ?catalog dcat:dataset ?dataSet . " +
                    "  } " +
                    "}");

            pss.setNsPrefixes(PREFIXES);
            pss.setParam("dataSet", dataSet);

            try (QueryExecution queryExecution =
                         this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
                ResultSet resultSet = queryExecution.execSelect();
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    catalog = solution.getResource("catalog");
                    logger.trace("get catalog: {}", catalog);
                }
            } catch (Exception ex) {
                logger.error("Exception in executing select ", ex);
            }
        } catch (Exception e) {
            logger.error("Exception in getting the catalog ", e);
        }
        return catalog;
    }

    private Model getAllPredicatesObjectsPublisherDistributions(Resource dataSet) {

        Model model;

        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "CONSTRUCT { " + "?dataSet ?predicate ?object . " +
                "?object ?p2 ?o2} " +
                "WHERE { " +
                "  GRAPH ?g { " +
                "    ?dataSet ?predicate ?object. " +
                "    OPTIONAL { ?object ?p2 ?o2 } " +
                "  } " +
                "}");

        pss.setNsPrefixes(PREFIXES);
        pss.setParam("dataSet", dataSet);

        model = executeConstruct(pss);

        return model;
    }


    private Model executeConstruct(ParameterizedSparqlString pss) {
        Model model = null;
        try (QueryExecution queryExecution =
                     this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            model = queryExecution.execConstruct();
        } catch (Exception ex) {
            logger.error("Exception in executing construct ", ex);
        }
        return model;
    }

    /**
     * @return -1 => something went wrong, o.w. the number of distinct dataSets are return
     */
    private int getTotalNumberOfDataSets() {
        int cnt;
        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "SELECT (COUNT(DISTINCT ?dataSet) AS ?num)\n" +
                "WHERE { \n" +
                "  GRAPH ?g {\n" +
                "    ?dataSet a dcat:Dataset.\n" +
                "    FILTER(EXISTS{?dataSet dct:title ?title.})\n" +
                "  }\n" +
                "}");

        pss.setNsPrefixes(PREFIXES);

        cnt = getCount(pss);
        return cnt;
    }


    private List<Resource> getListOfDataSets(int idx, int limit) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "SELECT DISTINCT ?dataSet\n" +
                "WHERE { \n" +
                "  GRAPH ?g {\n" +
                "    ?dataSet a dcat:Dataset.\n" +
                "    FILTER(EXISTS{?dataSet dct:title ?title.})\n" +
                "  }\n" +
                "}\n" +
                "ORDER BY ?dataSet\n" +
                "OFFSET \n" + idx +
                "LIMIT " + limit
        );

        pss.setNsPrefixes(PREFIXES);

        return getResources(pss);
    }

    private List<Resource> getResources(ParameterizedSparqlString pss) {
        List<Resource> ret = new ArrayList<>();
        try (QueryExecution queryExecution =
                     this.queryExecutionFactoryHttpProvider.getQef().createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                Resource dataSet = solution.getResource("dataSet");
                ret.add(dataSet);
                logger.trace("getResource: {}", dataSet);
            }
        } catch (Exception ex) {
            logger.error("Exception in getting resources ", ex);
        }
        return ret;
    }

    private int getCount(ParameterizedSparqlString pss) {
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
            logger.error("Exception in getting Count ", ex);
        }
        return cnt;
    }

}
