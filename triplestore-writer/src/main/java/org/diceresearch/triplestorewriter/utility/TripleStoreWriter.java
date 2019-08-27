package org.diceresearch.triplestorewriter.utility;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class TripleStoreWriter implements CredentialsProvider {

    private static final Logger logger = LoggerFactory.getLogger(TripleStoreWriter.class);

    @Value("${info.opal.tripleStore.url}")
    private String tripleStoreURL;
    @Value("${info.opal.tripleStore.username}")
    private String tripleStoreUsername;
    @Value("${info.opal.tripleStore.password}")
    private String tripleStorePassword;

    private org.apache.http.impl.client.CloseableHttpClient client;
    private org.apache.http.auth.Credentials credentials;

    @PostConstruct
    public void initialize() {
        this.credentials = new UsernamePasswordCredentials(tripleStoreUsername, tripleStorePassword);
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultCredentialsProvider(this);
        client = clientBuilder.build();
    }

    public void write(byte[] bytes) {
        if (bytes == null) return;
        writeModel(bytes);
    }

    private void writeModel(byte[] bytes) {
        Model model = RdfSerializerDeserializer.deserialize(bytes);
        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                logger.info("{}", kv("datasetUrl", dataSet.getURI()));
            }
        } catch (Exception ignored) {
        }
        StmtIterator stmtIterator = model.listStatements();
        QuerySolutionMap mp = new QuerySolutionMap();
        int cnt = 0;
        StringBuilder triples = new StringBuilder();
        while (stmtIterator.hasNext()) {
            if (cnt > 50) {
                runWriteQuery(triples, mp);
                triples = new StringBuilder();
                mp = new QuerySolutionMap();
                cnt = 0;
            }
            Statement statement = stmtIterator.nextStatement();

            String s = "?s" + cnt;
            String p = "?p" + cnt;
            String o = "?o" + cnt;

            cnt++;

            mp.add(s, statement.getSubject());
            mp.add(p, statement.getPredicate());
            mp.add(o, statement.getObject());

            triples
                    .append(s).append(' ')
                    .append(p).append(' ')
                    .append(o).append(" . ");
        }
        runWriteQuery(triples, mp);
    }


    private void runWriteQuery(StringBuilder triples, QuerySolutionMap mp) {
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString("INSERT DATA { GRAPH <http://projekt-opal.de> {" + triples + "} }");
            pss.setParams(mp);

            String query = pss.toString();
            query = new String(query.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8); // TODO: 17.04.19 check to make sure that it is OK
            logger.debug("writing query is: {}", query);
            runInsertQuery(query);
        } catch (Exception e) {
            logger.error("An error occurred in writing to TripleStore ", e);
        }
    }

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 5000))
    private void runInsertQuery(String query) {
        UpdateRequest request = UpdateFactory.create(query);
        UpdateProcessor proc = UpdateExecutionFactory.createRemoteForm(request, tripleStoreURL, client);
        proc.execute();
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
