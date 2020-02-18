package org.diceresearch.elasticsearchwriter.utility.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.dice_research.opal.common.vocabulary.Opal;
import org.diceresearch.elasticsearchwriter.entity.DataSet;
import org.diceresearch.elasticsearchwriter.utility.ElasticSearchWriter;
import org.diceresearch.elasticsearchwriter.utility.ModelMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ElasticSearchWriterImpl implements ElasticSearchWriter {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public ElasticSearchWriterImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void write(byte[] bytes) {
        Model model = ModelSerialization.deserialize(bytes);
        log.trace("called: write, {}", StructuredArguments.kv("model", model));

        try {
            logModel(model);
            DataSet dataSet = ModelMapper.toDataset(model);
            Gson gson = new Gson();
            String json = gson.toJson(dataSet);
            log.info("json={}", json);
            IndexRequest indexRequest = new IndexRequest("opal").source(json, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.debug("Elastic Writer: {}", indexResponse.toString());
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    private void logModel(Model model) {
        Resource dataSet;
        ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
        if (resIterator.hasNext()) {
            dataSet = resIterator.nextResource();
            String originalUriValue = "";
            try {
                NodeIterator nodeIterator = model.listObjectsOfProperty(dataSet, Opal.PROP_ORIGINAL_URI);
                if (nodeIterator.hasNext()) originalUriValue = nodeIterator.next().toString();
            } catch (Exception ignored) {
            }
            log.info("{} {}", StructuredArguments.kv("originalUri", originalUriValue),
                    StructuredArguments.kv("dataSetUri", dataSet.getURI()));
        }
    }

}
