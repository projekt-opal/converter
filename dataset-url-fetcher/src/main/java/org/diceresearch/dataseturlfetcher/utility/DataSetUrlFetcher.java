package org.diceresearch.dataseturlfetcher.utility;

import com.google.common.collect.ImmutableMap;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;
import org.diceresearch.dataseturlfetcher.model.Portal;
import org.diceresearch.dataseturlfetcher.repository.PortalRepository;
import org.diceresearch.dataseturlfetcher.messaging.ResourceSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

@Component
@Scope("prototype")
@EnableRetry
public class DataSetUrlFetcher implements CredentialsProvider, Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DataSetUrlFetcher.class);

    private org.aksw.jena_sparql_api.core.QueryExecutionFactory qef;
    private boolean isCanceled;

    @Value("${info.crawler.tripleStore.url}")
    private String tripleStoreURL;
    @Value("${info.crawler.tripleStore.username}")
    private String tripleStoreUsername;
    @Value("${info.crawler.tripleStore.password}")
    private String tripleStorePassword;

    private org.apache.http.auth.Credentials credentials;

    private static final int PAGE_SIZE = 200;

    private static final ImmutableMap<String, String> PREFIXES = ImmutableMap.<String, String>builder()
            .put("dcat", "http://www.w3.org/ns/dcat#")
            .put("dct", "http://purl.org/dc/terms/")
            .build();


    private final PortalRepository portalRepository;
    private final ResourceSender resourceSender;
    private String portalName;

    @Autowired
    public DataSetUrlFetcher(PortalRepository portalRepository, ResourceSender resourceSender) {
        this.portalRepository = portalRepository;
        this.resourceSender = resourceSender;
    }

    public void run() {
        try {
            logger.info("Start fetching {}", kv("portalName",portalName));
            initialQueryExecutionFactory(portalName);

            int totalNumberOfDataSets = getTotalNumberOfDataSets();
            logger.debug("Total number of datasets is {}", kv("Total #Datasets",totalNumberOfDataSets));
            if (totalNumberOfDataSets == -1) {
                throw new Exception("Cannot Query the TripleStore");
            }
            Portal portal = portalRepository.findByName(portalName);
            int high = portal.getHigh();
            int lnf = portal.getLastNotFetched();

            if (high == -1 || high > totalNumberOfDataSets) {
                high = totalNumberOfDataSets;
            }

            portal.setHigh(high);
            portalRepository.save(portal);

            for (int idx = lnf; idx < high; idx += PAGE_SIZE) {
                if (isCanceled) {
                    logger.info("fetching portal {} is cancelled", portalName);
                    return;
                }
                portal.setLastNotFetched(idx);
                portalRepository.save(portal);

                int min = Math.min(PAGE_SIZE, high - idx);
                logger.info("Getting list datasets  {} : {}", idx, idx + min);
                List<Resource> listOfDataSets = getListOfDataSets(idx, min);
                listOfDataSets
//                        .subList(1,2) //only for debug
                        .parallelStream().forEach(resource -> resourceSender.send(resource, portal)); // TODO: 31.07.19 Enqueue
            }
            portal.setLastNotFetched(high);
            portalRepository.save(portal);


            logger.info("fetching portal {} finished", portalName);
        } catch (Exception e) {
            logger.error("An Error occurred in converting portal {}, {}", portalName, e);
        }
    }

    private void initialQueryExecutionFactory(String portal) {
        credentials = new UsernamePasswordCredentials(tripleStoreUsername, tripleStorePassword);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultCredentialsProvider(this);
        org.apache.http.impl.client.CloseableHttpClient client = clientBuilder.build();


        qef = new QueryExecutionFactoryHttp(
                String.format(tripleStoreURL, portal),
                new org.apache.jena.sparql.core.DatasetDescription(), client);
        qef = new QueryExecutionFactoryRetry(qef, 5, 1000);

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
        try (QueryExecution queryExecution = qef.createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                Resource dataSet = solution.getResource("dataSet");
                ret.add(dataSet);
                logger.trace("getResource: {}", dataSet);
            }
        } catch (Exception ex) {
            logger.error("An error occurred in getting resources ", ex);
        }
        return ret;
    }

    private int getCount(ParameterizedSparqlString pss) {
        int cnt = -1;
        try (QueryExecution queryExecution = qef.createQueryExecution(pss.asQuery())) {
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution solution = resultSet.nextSolution();
                RDFNode num = solution.get("num");
                cnt = num.asLiteral().getInt();
            }
        } catch (Exception ex) {
            logger.error("An error occurred in getting Count, {}", ex);
        }
        return cnt;
    }

    public DataSetUrlFetcher setPortalName(String portalName) {
        this.portalName = portalName;
        return this;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public void setCredentials(AuthScope authScope, Credentials credentials) {

    }

    @Override
    public Credentials getCredentials(AuthScope authScope) {
        return credentials;
    }

    @Override
    public void clear() {

    }
}
