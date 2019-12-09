package org.diceresearch.opalconfirmconversionservice.utility;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.common.utilities.Hash;
import org.dice_research.opal.common.utilities.ModelSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.dice_research.opal.common.vocabulary.Opal.NS_OPAL;
import static org.dice_research.opal.common.vocabulary.Opal.originalUri;

//import org.dice_research.opal.common.utilities.Hash;
//import org.diceresearch.common.utility.rdf.RdfSerializerDeserializer;

@Component
public class OpalConfirmer {

    private static final Logger logger = LoggerFactory.getLogger(OpalConfirmer.class);

    public byte[] convert(byte[] bytes) {
        Model model;
        try {
            model = ModelSerialization.deserialize(bytes);
        } catch (Exception e) {
            logger.error("Exception in deserialize the byte code ", e);
            return bytes;
        }
        try {
            ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
            if (resIterator.hasNext()) {
                Resource dataSet = resIterator.nextResource();
                model.add(dataSet, originalUri, dataSet);
                logger.info("{}", kv("datasetUrl", dataSet.getURI()));
                // After the dataset URI is changed to Opal format, new URI to be passed for distribution
                Resource catalog = getCatalog(model);
                dataSet = makeOpalConfirmedUri(model, catalog, dataSet, DCAT.Dataset, null, "dataset");
                makeOpalConfirmedUri(model, catalog, dataSet, DCAT.Distribution, DCAT.distribution, "distribution");
                ResIterator opalConfirmedIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
                Resource dataSetOpalConfirmed = opalConfirmedIterator.nextResource();// TODO: 07.12.18 Check for Exception (".nextResource()")
                updateDatasetInGraph(dataSet, dataSetOpalConfirmed, model, catalog);
                //removing duplicate catalog info (if it is there)
                StmtIterator stmtIterator = model.listStatements(dataSetOpalConfirmed,
                        ResourceFactory.createProperty("http://www.w3.org/ns/dcat#catalog"), (RDFNode) null);
                model.remove(stmtIterator);

                return ModelSerialization.serialize(model);
            } else {
                logger.info("The given model doesn't have DCAT:Dataset");
            }
        } catch (Exception e) {
            logger.error("Exception in converting th model", e);
        }

        return bytes;
    }

    private void updateDatasetInGraph(Resource dataSet, Resource dataSetOpalConfirmed, Model model, Resource portal) {
        model.remove(portal, DCAT.dataset, dataSet);
        model.add(portal, DCAT.dataset, dataSetOpalConfirmed);
    }

    private Resource getCatalog(Model model) {
        ResIterator iterator = model.listSubjectsWithProperty(RDF.type, DCAT.Catalog);
        if (iterator.hasNext()) {
            return iterator.nextResource();
        }
        return null;
    }

    private boolean isNotOpalConfirmed(String uri) {
        return !uri.startsWith(NS_OPAL);
    }

    private Resource makeOpalConfirmedUri(Model model, Resource catalog, Resource dataSet, Resource classType, Property propertyType, String typeName) {
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
        String s = resource.getURI();
        if (type.equals("dataset")) {
            s = catalog.getLocalName().concat(s);
            s = Hash.md5(s);
        } else {
            String pattern = "[^a-zA-Z0-9]";
            s = s.replaceAll(pattern, "_");
        }
        return ResourceFactory.createResource(NS_OPAL + type + "/" + s);
    }
}
