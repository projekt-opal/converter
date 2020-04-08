package org.diceresearch.datasetfilefetcher;

import org.diceresearch.datasetfilefetcher.utility.FileFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

@SpringBootApplication
@EnableEurekaClient
@EnableBinding(Source.class)
public class DatasetFileFetcherApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DatasetFileFetcherApplication.class, args);
    }

    @Autowired
    private FileFetcher fileFetcher;

    @Override
    public void run(String... args) throws Exception {
        fileFetcher.fetch();
    }
}
