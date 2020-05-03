package org.diceresearch.opalconfirmconversionservice.utility;

import net.logstash.logback.argument.StructuredArguments;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.Hash;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.dice_research.opal.common.vocabulary.Opal;
import org.dice_research.opal.metadata.GeoData;
import org.dice_research.opal.metadata.LanguageDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.dice_research.opal.common.vocabulary.Opal.NS_OPAL;

@Component
public class OpalConfirmer {

    private static final Logger logger = LoggerFactory.getLogger(OpalConfirmer.class);

    private static final Property catalogProperty = ResourceFactory.createProperty("http://www.w3.org/ns/dcat#catalog");

    public byte[] convert(byte[] bytes) {
        Model model;
        try {
            model = ModelSerialization.deserialize(bytes);
            logger.trace("called: convert, {}", StructuredArguments.kv("model", model));
        } catch (Exception e) {
            logger.error("Exception in deserialize the byte code ", e);
            return bytes;
        }
        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                model.add(dataSet, Opal.PROP_ORIGINAL_URI, dataSet);
                String originalUriValue = dataSet.getURI();
                // After the dataset URI is changed to Opal format, new URI to be passed for distribution
                Resource catalog = getCatalog(model);
                dataSet = makeOpalConfirmedUri(model, catalog, dataSet, DCAT.Dataset, null, "dataset");
                logger.info("{} {}", StructuredArguments.kv("originalUri", originalUriValue), StructuredArguments.kv("dataSetUri", dataSet.getURI()));

                makeOpalConfirmedUri(model, catalog, dataSet, DCAT.Distribution, DCAT.distribution, "distribution");

                StmtIterator stmtIterator = model.listStatements(dataSet, catalogProperty, (RDFNode) null);
                model.remove(stmtIterator);

                refineMetadata(model, dataSet);

                createGeoData(model, dataSet);

                logger.trace("return: convert, {}", StructuredArguments.kv("model", model));
                return ModelSerialization.serialize(model);
            } else {
                logger.info("The given model doesn't have DCAT:Dataset");
            }
        } catch (Exception e) {
            logger.error("Exception in converting th model", e);
        }

        return bytes;
    }

    private void createGeoData(Model model, Resource dataSet) {
        logger.trace("called: createGeoData, {}, {}", StructuredArguments.kv("model", model),
                StructuredArguments.kv("dataSet", dataSet));
        try {
            new GeoData().processModel(model, dataSet.getURI());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void refineMetadata(Model model, Resource dataSet) {
        logger.trace("called: refineMetadata, {}, {}", StructuredArguments.kv("model", model),
                StructuredArguments.kv("dataSet", dataSet));
        try {
            LanguageDetection languageDetection = new LanguageDetection();
            languageDetection.initialize();

            // Update model
            languageDetection.processModel(model, dataSet.getURI());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private Resource getCatalog(Model model) {
        logger.trace("called: getCatalog, {}", StructuredArguments.kv("model", model));
        ResIterator iterator = model.listSubjectsWithProperty(RDF.type, DCAT.Catalog);
        Resource catalog = null;
        if (iterator.hasNext()) {
            catalog = iterator.nextResource();
        }
        logger.trace("return: getCatalog, {}, {}", StructuredArguments.kv("model", model), StructuredArguments.kv("catalog", catalog));
        return catalog;
    }

    private boolean isNotOpalConfirmed(String uri) {
        logger.trace("called: isNotOpalConfirmed, {}", StructuredArguments.kv("uri", uri));
        return !uri.startsWith(NS_OPAL);
    }

    private Resource makeOpalConfirmedUri(Model model, Resource catalog, Resource dataSet, Resource classType,
                                          Property propertyType, String typeName) {
        logger.trace("called: makeOpalConfirmedUri, {}, {}, {}, {}, {}, {}",
                StructuredArguments.kv("model", model), StructuredArguments.kv("catalog", catalog),
                StructuredArguments.kv("dataSet", dataSet), StructuredArguments.kv("classType", classType),
                StructuredArguments.kv("propertyType", propertyType), StructuredArguments.kv("typeName", typeName));

        ResIterator resIterator = model.listResourcesWithProperty(RDF.type, classType);
        Resource newResource = null;
        while (resIterator.hasNext()) {
            Resource oldResource = resIterator.nextResource();
            if (isNotOpalConfirmed(oldResource.getURI())) {
                newResource = generateOpalConfirmedUrl(catalog, oldResource, typeName);

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

    private Resource generateOpalConfirmedUrl(Resource catalog, Resource resource, String type) {
        logger.trace("called: generateOpalConfirmedUrl, {}, {}, {}", StructuredArguments.kv("catalog", catalog),
                StructuredArguments.kv("resource", resource), StructuredArguments.kv("type", type));
        String s = resource.getURI();
        s = catalog.getLocalName().concat(s);
        s = Hash.md5(s);
        String confirmedUri = NS_OPAL + type + "/" + s;
        logger.trace("return: generateOpalConfirmedUrl, {}, {}, {}, {}",
                StructuredArguments.kv("confirmedUri", confirmedUri), StructuredArguments.kv("catalog", catalog),
                StructuredArguments.kv("resource", resource), StructuredArguments.kv("type", type));

        return ResourceFactory.createResource(confirmedUri);
    }
}
