package org.diceresearch.datasetfilefetcher.utility;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.diceresearch.datasetfilefetcher.messaging.SourceWithDynamicDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class FileFetcher {

    private static final Logger logger = LoggerFactory.getLogger(FileFetcher.class);
    private final SourceWithDynamicDestination sourceWithDynamicDestination;
    @Value("${RDF_FILES_PATH}")
    private String path;
    private String outputQueue = "dataset-graph";

    @Autowired
    public FileFetcher(SourceWithDynamicDestination sourceWithDynamicDestination) {
        this.sourceWithDynamicDestination = sourceWithDynamicDestination;
    }

    public void fetch() {
        Path dir = Paths.get(path);
        try {
            Stream<Path> list = Files.list(dir);
            list.forEach(file -> {
                try {
                    Model model = ModelFactory.createDefaultModel();
                    model.read(file.toString());
                    byte[] serialize = ModelSerialization.serialize(model);
                    sourceWithDynamicDestination.sendMessage(serialize, outputQueue);
                } catch (Exception e) {
                    System.out.println("error");
                }
            });
        } catch (IOException e) {
            logger.error("", e);
        }
    }

}
