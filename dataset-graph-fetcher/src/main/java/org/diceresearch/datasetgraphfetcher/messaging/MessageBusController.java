package org.diceresearch.datasetgraphfetcher.messaging;

import org.apache.jena.rdf.model.Model;
import org.diceresearch.common.utility.Pair;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.diceresearch.datasetgraphfetcher.utility.DataSetGraphFetcher;
import org.diceresearch.datasetgraphfetcher.utility.DataSetGraphFetcherPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;

@Service
public class MessageBusController {

    private final DataSetGraphFetcherPool dataSetGraphFetcherPool;

    @Autowired
    public MessageBusController(DataSetGraphFetcherPool dataSetGraphFetcherPool) {
        this.dataSetGraphFetcherPool = dataSetGraphFetcherPool;
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public byte[] getDatasetGraph(Pair<String, String> pair) {
        DataSetGraphFetcher fetcher = dataSetGraphFetcherPool.getFetcher(pair.getValue());
        Model model = fetcher.getGraph(pair.getKey());
        return RdfSerializerDeserializer.serialize(model);
    }

}
