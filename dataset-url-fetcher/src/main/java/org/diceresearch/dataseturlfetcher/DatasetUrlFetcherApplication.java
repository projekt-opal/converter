package org.diceresearch.dataseturlfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DatasetUrlFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasetUrlFetcherApplication.class, args);
    }

}
