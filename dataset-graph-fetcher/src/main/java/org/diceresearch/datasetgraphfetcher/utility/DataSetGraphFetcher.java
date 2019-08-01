package org.diceresearch.datasetgraphfetcher.utility;

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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@EnableRetry
public class DataSetGraphFetcher implements CredentialsProvider {
    private static final Logger logger = LoggerFactory.getLogger(DataSetGraphFetcher.class);

    private static final Property opalTemporalCatalogProperty =
            ResourceFactory.createProperty("http://projekt-opal.de/catalog");
    private org.aksw.jena_sparql_api.core.QueryExecutionFactory qef;

    @Value("${info.crawler.tripleStore.url}")
    private String tripleStoreURL;
    @Value("${info.crawler.tripleStore.username}")
    private String tripleStoreUsername;
    @Value("${info.crawler.tripleStore.password}")
    private String tripleStorePassword;

    private Credentials credentials;

    private static final ImmutableMap<String, String> PREFIXES = ImmutableMap.<String, String>builder()
            .put("dcat", "http://www.w3.org/ns/dcat#")
            .put("dct", "http://purl.org/dc/terms/")
            .build();

    private Resource portalResource;

    void initialQueryExecutionFactory(String portalName) {

        portalResource = ResourceFactory.createResource("http://projekt-opal.de/catalog/" + portalName);
        credentials = new UsernamePasswordCredentials(tripleStoreUsername, tripleStorePassword);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultCredentialsProvider(this);
        org.apache.http.impl.client.CloseableHttpClient client = clientBuilder.build();


        qef = new QueryExecutionFactoryHttp(
                String.format(tripleStoreURL, portalName),
                new org.apache.jena.sparql.core.DatasetDescription(), client);
        qef = new QueryExecutionFactoryRetry(qef, 5, 1000);

    }


    public Model getGraph(String resourceUri) {
        Resource dataSet = ResourceFactory.createResource(resourceUri);

        Model dataSetGraph = getAllPredicatesObjectsPublisherDistributions(dataSet);
        dataSetGraph.add(dataSet, opalTemporalCatalogProperty, portalResource);
        return dataSetGraph;
    }

    private Model getAllPredicatesObjectsPublisherDistributions(Resource dataSet) {

        Model model;

        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "CONSTRUCT { " + "?dataSet ?predicate ?object .\n" +
                "\t?object ?p2 ?o2}\n" +
                "WHERE { \n" +
                "  GRAPH ?g {\n" +
                "    ?dataSet ?predicate ?object.\n" +
                "    OPTIONAL { ?object ?p2 ?o2 }\n" +
                "  }\n" +
                "}");

        pss.setNsPrefixes(PREFIXES);
        pss.setParam("dataSet", dataSet);

        model = executeConstruct(pss);

        return model;
    }


    private Model executeConstruct(ParameterizedSparqlString pss) {
        Model model = null;
        try (QueryExecution queryExecution = qef.createQueryExecution(pss.asQuery())) {
            model = queryExecution.execConstruct();
        } catch (Exception ex) {
            logger.error("An error occurred in executing construct ", ex);
        }
        return model;
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
