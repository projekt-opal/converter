package org.diceresearch.datasetgraphfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Source;

@SpringBootApplication
@EnableEurekaClient
@EnableBinding(Processor.class)
public class DatasetGraphFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasetGraphFetcherApplication.class, args);
    }

}
