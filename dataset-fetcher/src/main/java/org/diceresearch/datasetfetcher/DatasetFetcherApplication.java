package org.diceresearch.datasetfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DatasetFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasetFetcherApplication.class, args);
    }

}
