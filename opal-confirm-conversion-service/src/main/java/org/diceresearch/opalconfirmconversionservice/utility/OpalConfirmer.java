package org.diceresearch.opalconfirmconversionservice.utility;

import org.aksw.jena_sparql_api.mapper.annotation.RdfType;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class OpalConfirmer {

    private static final Logger logger = LoggerFactory.getLogger(OpalConfirmer.class);
    private static final Property opalTemporalCatalogProperty =
            ResourceFactory.createProperty("http://projekt-opal.de/catalog");

    public byte[] convert(byte[] bytes) {
        Model model;
        try {
            model = RdfSerializerDeserializer.deserialize(bytes);
        } catch (Exception e) {
            logger.error("Exception in deserialize the byte code ", e);
            return bytes;
        }
        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                logger.info("{}", kv("datasetUrl", dataSet.getURI()));
                // After the dataset URI is changed to Opal format, new URI to be passed for distribution
                dataSet = makeOpalConfirmedUri(model, dataSet, DCAT.Dataset, null, "dataset");
                makeOpalConfirmedUri(model, dataSet, DCAT.Distribution, DCAT.distribution, "distribution");
                ResIterator opalConfirmedIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
                Resource dataSetOpalConfirmed = opalConfirmedIterator.nextResource();// TODO: 07.12.18 Check for Exception (".nextResource()")
                updateDatasetInGraph(dataSet, dataSetOpalConfirmed, model);
                //removing duplicate catalog info (if it is there)
                StmtIterator stmtIterator = model.listStatements(dataSetOpalConfirmed,
                        ResourceFactory.createProperty("http://www.w3.org/ns/dcat#catalog"), (RDFNode) null);
                model.remove(stmtIterator);

                return RdfSerializerDeserializer.serialize(model);
            } else {
                logger.info("The given model doesn't have DCAT:Dataset");
            }
        } catch (Exception e) {
            logger.error("Exception in converting th model", e);
        }

        return bytes;
    }

    private void updateDatasetInGraph(Resource dataSet, Resource dataSetOpalConfirmed, Model model) {
        ResIterator iterator = model.listSubjectsWithProperty(RDF.type, DCAT.Catalog);
        if (iterator.hasNext()) {
            Resource portal = iterator.nextResource();
            model.remove(portal, DCAT.dataset, dataSet);
            model.add(portal, DCAT.dataset, dataSetOpalConfirmed);
        }
    }

    private boolean isNotOpalConfirmed(String uri) {
        return !uri.startsWith("http://projekt-opal.de/");
    }

    private Resource makeOpalConfirmedUri(Model model, Resource dataSet, Resource classType, Property propertyType, String typeName) {
        ResIterator resIterator = model.listResourcesWithProperty(RDF.type, classType);
        Resource newResource = null;
        while (resIterator.hasNext()) {
            Resource oldResource = resIterator.nextResource();
            if (isNotOpalConfirmed(oldResource.getURI())) {
                newResource = generateOpalConfirmedUrl(oldResource, typeName);

                StmtIterator oldIterator = model.listStatements(new SelectorImpl(oldResource, null, (RDFNode) null));
                List<Statement> newResourceStatements = new ArrayList<>();
                while (oldIterator.hasNext()) {
                    Statement statement = oldIterator.nextStatement();
                    newResourceStatements.add(new StatementImpl(newResource, statement.getPredicate(), statement.getObject()));
                }
                oldIterator = model.listStatements(new SelectorImpl(oldResource, null, (RDFNode) null));
                model.remove(oldIterator);
                model.add(newResourceStatements);

                if (propertyType != null) {
                    model.remove(dataSet, propertyType, oldResource);
                    model.add(dataSet, propertyType, newResource);
                }
            } else { // for mcloud portal
                newResource = dataSet;
            }
        }
        return newResource;
    }

    private Resource generateOpalConfirmedUrl(Resource resource, String type) {
        String uri = resource.getURI();
        String pattern = "[^a-zA-Z0-9]";
        String s = uri.replaceAll(pattern, "_");
        return ResourceFactory.createResource("http://projekt-opal.de/" + type + "/" + s);
    }
}
