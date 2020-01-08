package org.diceresearch.elasticsearchwriter.utility.impl;

import net.logstash.logback.argument.StructuredArguments;
import org.apache.http.HttpHost;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.dice_research.opal.common.vocabulary.Dqv;
import org.dice_research.opal.common.vocabulary.Opal;
import org.diceresearch.elasticsearchwriter.utility.ElasticSearchWriter;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

@Component
public class ElasticSearchWriterImpl implements ElasticSearchWriter {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchWriter.class);

    @Value("${info.opal.elasticsearch.url}")
    private String ELASTIC_SEARCH_URL;

    @Value("${info.opal.elasticsearch.port}")
    private String ELASTIC_SEARCH_PORT;

    private RestHighLevelClient restClient;

    @PostConstruct
    public void init() {
        logger.trace("calld: init, {}, {}", StructuredArguments.kv("url", ELASTIC_SEARCH_URL),
                StructuredArguments.kv("port", ELASTIC_SEARCH_PORT));

        restClient = new RestHighLevelClient(RestClient.builder
                (new HttpHost(ELASTIC_SEARCH_URL, Integer.parseInt(ELASTIC_SEARCH_PORT), "http")));
    }

    @Override
    public void write(byte[] bytes) {
        Model model = ModelSerialization.deserialize(bytes);
        logger.trace("called: write, {}", StructuredArguments.kv("model.graph", model.getGraph()));

        Resource dataSet = null;

        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                dataSet = resIterator.nextResource();
                String originalUriValue = "";
                try {
                    NodeIterator nodeIterator = model.listObjectsOfProperty(dataSet, Opal.originalUri);
                    if (nodeIterator.hasNext()) originalUriValue = nodeIterator.next().toString();
                } catch (Exception ignored) {
                }
                logger.info("{} {}", StructuredArguments.kv("originalUri", originalUriValue),
                        StructuredArguments.kv("dataSetUri", dataSet.getURI()));

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] datasetHashId = md.digest(dataSet.getURI().getBytes());
                String datasetHashedString = DatatypeConverter.printHexBinary(datasetHashId).toUpperCase();
                JSONObject jsonDatasetObject = new JSONObject();

                jsonDatasetObject.put("URI", dataSet.getURI());

                StmtIterator titleIterator = model.listStatements(dataSet, DCTerms.title, (RDFNode) null);
                if (titleIterator.hasNext()) {
                    Statement titleStatement = titleIterator.nextStatement();
                    jsonDatasetObject.put(titleStatement.getPredicate().getLocalName(), titleStatement.getObject().asLiteral().getValue());
                }

                StmtIterator descIterator = model.listStatements(dataSet, DCTerms.description, (RDFNode) null);
                if (descIterator.hasNext()) {
                    Statement descStatement = descIterator.nextStatement();
                    jsonDatasetObject.put(descStatement.getPredicate().getLocalName(), descStatement.getObject().asLiteral().getValue());
                }

                StmtIterator dateIssuedIterator = model.listStatements(dataSet, DCTerms.issued, (RDFNode) null);
                if (dateIssuedIterator.hasNext()) {
                    Statement dateIssuedStatement = dateIssuedIterator.nextStatement();
                    jsonDatasetObject.put(dateIssuedStatement.getPredicate().getLocalName(), dateIssuedStatement.getObject().asLiteral().getString());
                }

                StmtIterator dateModifiedIterator = model.listStatements(dataSet, DCTerms.modified, (RDFNode) null);
                if (dateModifiedIterator.hasNext()) {
                    Statement dateModifiedStatement = dateModifiedIterator.nextStatement();
                    jsonDatasetObject.put(dateModifiedStatement.getPredicate().getLocalName(), dateModifiedStatement.getObject().asLiteral().getString());
                }

                StmtIterator identifierIterator = model.listStatements(dataSet, DCTerms.identifier, (RDFNode) null);
                if (identifierIterator.hasNext()) {
                    Statement identifierStatement = identifierIterator.nextStatement();
                    jsonDatasetObject.put(identifierStatement.getPredicate().getLocalName(), identifierStatement.getObject().toString());
                }

                StmtIterator accrualPeriodicityIterator = model.listStatements(dataSet, DCTerms.accrualPeriodicity, (RDFNode) null);
                if (accrualPeriodicityIterator.hasNext()) {
                    Statement accrualPeriodicityStatement = accrualPeriodicityIterator.nextStatement();
                    jsonDatasetObject.put(accrualPeriodicityStatement.getPredicate().getLocalName(), accrualPeriodicityStatement.getObject().toString());
                }

                StmtIterator landingPageIterator = model.listStatements(dataSet, DCAT.landingPage, (RDFNode) null);
                if (landingPageIterator.hasNext()) {
                    Statement landingPageStatement = landingPageIterator.nextStatement();
                    jsonDatasetObject.put(landingPageStatement.getPredicate().getLocalName(), landingPageStatement.getObject().toString());
                }

                StmtIterator languageIterator = model.listStatements(dataSet, DCTerms.language, (RDFNode) null);
                if (languageIterator.hasNext()) {
                    Statement languageStatement = languageIterator.nextStatement();
                    jsonDatasetObject.put(languageStatement.getPredicate().getLocalName(), languageStatement.getObject().toString());
                }

                StmtIterator licenseIterator = model.listStatements(dataSet, DCTerms.license, (RDFNode) null);
                if (licenseIterator.hasNext()) {
                    Statement licenseStatement = licenseIterator.nextStatement();
                    jsonDatasetObject.put(licenseStatement.getPredicate().getLocalName(), licenseStatement.getObject().toString());
                }

                JSONArray distributions = getJSONArray(model, dataSet, DCAT.distribution, DCAT.distribution.getLocalName());
                jsonDatasetObject.put("distributions", distributions);

                JSONArray hasQualityMeasurements = getJSONArray(model, dataSet, Dqv.HAS_QUALITY_MEASUREMENT, Dqv.HAS_QUALITY_MEASUREMENT.getLocalName());
                jsonDatasetObject.put("hasQualityMeasurements", hasQualityMeasurements);

                JSONArray keywords = getJSONArray(model, dataSet, DCAT.keyword, DCAT.keyword.getLocalName());
                jsonDatasetObject.put("keywords", keywords);

                JSONArray publisher = getJSONArray(model, dataSet, DCTerms.publisher, DCTerms.publisher.getLocalName());
                jsonDatasetObject.put("publisherInfo", publisher);

                JSONArray contactPoints = getJSONArray(model, dataSet, DCAT.contactPoint, DCAT.contactPoint.getLocalName());
                jsonDatasetObject.put("contactPointInfo", contactPoints);

                JSONArray spatialInfo = getJSONArray(model, dataSet, DCTerms.spatial, DCTerms.spatial.getLocalName());
                jsonDatasetObject.put("spatialInfo", spatialInfo);

                JSONArray temporalInfo = getJSONArray(model, dataSet, DCTerms.temporal, DCTerms.temporal.getLocalName());
                jsonDatasetObject.put("temporalInfo", temporalInfo);

                JSONArray themeInfo = getJSONArray(model, dataSet, DCAT.theme, DCAT.theme.getLocalName());
                jsonDatasetObject.put("themes", themeInfo);

                IndexRequest indexRequest = new IndexRequest("opal", "_doc", datasetHashedString).
                        source(jsonDatasetObject, XContentType.JSON);
                IndexResponse indexResponse = restClient.index(indexRequest, RequestOptions.DEFAULT);
                logger.debug("Elastic Writer: {}", indexResponse.toString());

            }
        } catch (Exception exception) {
            if (dataSet != null)
                logger.error("exception {}", StructuredArguments.kv("datasetUrl", dataSet.getURI()), exception);
            else
                logger.error("exception", exception);
        }
    }

    private JSONArray getJSONArray(Model model, Resource dataSet, Property type, String field) {
        logger.trace("called: getJSONArray, {}, {}, {}, {}", StructuredArguments.kv("model", model),
                StructuredArguments.kv("dataSet", dataSet), StructuredArguments.kv("type", type),
                StructuredArguments.kv("field", field));

        StmtIterator propertyIterator = model.listStatements(dataSet, type, (RDFNode) null);
        JSONArray propertyArray = new JSONArray();
        while (propertyIterator.hasNext()) {
            Statement propertyStatement = propertyIterator.nextStatement();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(field, propertyStatement.getObject().toString());

            if (propertyStatement.getObject().isResource()) {

                StmtIterator propDetailsIterator = model.listStatements(propertyStatement.getObject().asResource(), null, (RDFNode) null);

                while (propDetailsIterator.hasNext()) {
                    Statement propDetailStatement = propDetailsIterator.nextStatement();
                    if (propDetailStatement.getObject().isResource())
                        jsonObject.put(propDetailStatement.getPredicate().getLocalName().toString(), propDetailStatement.getObject().toString());
                    else {
                        if (propDetailStatement.getPredicate().toString().startsWith("http://www.w3.org/ns/locn#geometry"))
                            jsonObject.put(propDetailStatement.getPredicate().getLocalName().toString(), propDetailStatement.getObject().asLiteral().getString());

                        else if (propDetailStatement.getPredicate().toString().startsWith("http://schema.org/endDate") ||
                                propDetailStatement.getPredicate().toString().startsWith("http://schema.org/startDate"))
                            jsonObject.put(propDetailStatement.getPredicate().getLocalName().toString(), propDetailStatement.getObject().asLiteral().getString());
                        else
                            jsonObject.put(propDetailStatement.getPredicate().getLocalName().toString(), propDetailStatement.getObject().asLiteral().getString());
                    }
                }
            }

            propertyArray.add(jsonObject);
        }
        return propertyArray;
    }
}
