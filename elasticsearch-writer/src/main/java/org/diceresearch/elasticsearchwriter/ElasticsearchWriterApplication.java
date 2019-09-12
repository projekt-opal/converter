package org.diceresearch.elasticsearchwriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
@EnableEurekaClient
@EnableBinding(Sink.class)
public class ElasticsearchWriterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchWriterApplication.class, args);
    }

}
