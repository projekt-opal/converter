package org.diceresearch.elasticsearchwriter.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ElasticSearchConfig {

    @Value("${OPAL_ELASTICSEARCH_URL}")
    private String hostname;
    @Value("${OPAL_ELASTICSEARCH_PORT}")
    private int port;

    @Bean(destroyMethod = "close")
    RestHighLevelClient getRestHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(hostname, port, "http")
        ));
    }

}
