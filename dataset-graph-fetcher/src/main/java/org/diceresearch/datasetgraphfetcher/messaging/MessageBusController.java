package org.diceresearch.datasetgraphfetcher.messaging;

import org.apache.jena.rdf.model.Model;
import org.diceresearch.common.utility.Pair;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.diceresearch.datasetgraphfetcher.utility.DataSetGraphFetcher;
import org.diceresearch.datasetgraphfetcher.utility.DataSetGraphFetcherPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class MessageBusController {

    private static final Logger logger = LoggerFactory.getLogger(MessageBusController.class);


    private final DataSetGraphFetcherPool dataSetGraphFetcherPool;

    @Autowired
    public MessageBusController(DataSetGraphFetcherPool dataSetGraphFetcherPool) {
        this.dataSetGraphFetcherPool = dataSetGraphFetcherPool;
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public byte[] getDatasetGraph(Pair<String, String> pair) {
        logger.info("{}", kv("datasetUrl", pair.getKey()));
        DataSetGraphFetcher fetcher = dataSetGraphFetcherPool.getFetcher(pair.getValue());
        Model model = fetcher.getGraph(pair.getKey());
        return RdfSerializerDeserializer.serialize(model);
    }

}
