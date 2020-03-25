package org.diceresearch.datasetfilefetcher;

import org.diceresearch.datasetfilefetcher.utility.FileFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
