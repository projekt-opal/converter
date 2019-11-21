package org.diceresearch.datasetfetcher.utility;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.retry.core.QueryExecutionFactoryRetry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class QueryExecutionFactoryHttpProvider implements CredentialsProvider {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutionFactoryHttpProvider.class);

    private final PortalRepository portalRepository;
    private org.apache.http.auth.Credentials credentials;
    private QueryExecutionFactory qef;

    @Autowired
    public QueryExecutionFactoryHttpProvider(PortalRepository portalRepository) {
        this.portalRepository = portalRepository;
    }

    void initialQueryExecutionFactory(Integer id) {
        Optional<Portal> optionalPortal = this.portalRepository.findById(id);
        if (!optionalPortal.isPresent()) return;
        Portal portal = optionalPortal.get();
        String url = portal.getQueryAddress();
        String username = portal.getUsername();
        String password = portal.getPassword();
        logger.info("TripleStore info: {} {}", kv("URL", url), kv("username", username));

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultCredentialsProvider(this);
        org.apache.http.impl.client.CloseableHttpClient client = clientBuilder.build();

        credentials = new UsernamePasswordCredentials(username, password);

        this.qef = new QueryExecutionFactoryHttp(url, new org.apache.jena.sparql.core.DatasetDescription(), client);
        this.qef = new QueryExecutionFactoryRetry(qef, 5, 1000);
    }

    QueryExecutionFactory getQef() {
        return qef;
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
