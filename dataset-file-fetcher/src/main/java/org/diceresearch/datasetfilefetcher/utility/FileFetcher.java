package org.diceresearch.datasetfilefetcher.utility;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
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
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileFetcher {

    private static final Logger logger = LoggerFactory.getLogger(FileFetcher.class);
    private static final String outputQueue = "dataset-graph";
    private final SourceWithDynamicDestination sourceWithDynamicDestination;
    @Value("${PORTAL}")
    private String portal;
    private int low = 0;
    private volatile boolean canceled = false;

    @Autowired
    public FileFetcher(SourceWithDynamicDestination sourceWithDynamicDestination) {
        this.sourceWithDynamicDestination = sourceWithDynamicDestination;
    }

    public void fetch(int startIndex, String folderPath) {
        canceled = false;
        Path dir = Paths.get(folderPath);
        Resource portalResource = ResourceFactory.createResource("http://projekt-opal.de/catalog/" + portal);
        try {
            List<Path> list = Files.list(dir).collect(Collectors.toList());
            for (low = startIndex; low < list.size(); low++) {
                Path file = list.get(low);
                if (canceled)
                    break;
                try {
                    Model model = ModelFactory.createDefaultModel();
                    model.read(file.toString(), "NT");
                    model.add(portalResource, RDF.type, DCAT.Catalog);
                    byte[] serialize = ModelSerialization.serialize(model);
                    System.out.println(low + ":" + model.size());
//                        sourceWithDynamicDestination.sendMessage(serialize, outputQueue);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public int getLow() {
        return low;
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
