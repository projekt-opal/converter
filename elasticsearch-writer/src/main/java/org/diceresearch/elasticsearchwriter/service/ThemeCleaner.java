package org.diceresearch.elasticsearchwriter.service;

import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ThemeCleaner {

    private static final Logger logger = LoggerFactory.getLogger(ThemeCleaner.class);

    private final String[] themes = new String[]{
            "http://publications.europa.eu/resource/authority/data-theme/AGRI",
            "http://publications.europa.eu/resource/authority/data-theme/EDUC",
            "http://publications.europa.eu/resource/authority/data-theme/ENVI",
            "http://publications.europa.eu/resource/authority/data-theme/ENER",
            "http://publications.europa.eu/resource/authority/data-theme/TRAN",
            "http://publications.europa.eu/resource/authority/data-theme/TECH",
            "http://publications.europa.eu/resource/authority/data-theme/ECON",
            "http://publications.europa.eu/resource/authority/data-theme/SOCI",
            "http://publications.europa.eu/resource/authority/data-theme/HEAL",
            "http://publications.europa.eu/resource/authority/data-theme/GOVE",
            "http://publications.europa.eu/resource/authority/data-theme/REGI",
            "http://publications.europa.eu/resource/authority/data-theme/JUST",
            "http://publications.europa.eu/resource/authority/data-theme/INTR",
            "http://publications.europa.eu/resource/authority/data-theme/OP_DATPRO"
    };

    public void clean(Model model) {

        ResIterator dataSetIterator = model.listSubjectsWithProperty(RDF.type, DCAT.Dataset);
        while (dataSetIterator.hasNext()) {
            RDFNode rdfNode = dataSetIterator.nextResource();
            if (rdfNode.isResource()) {
                cleanThemes(model, rdfNode.asResource());
            }
        }
    }

    private void cleanThemes(Model model, Resource dataSet) {
        List<Statement> toBeAdded = new ArrayList<>();

        NodeIterator nodeIterator = model.listObjectsOfProperty(dataSet, DCAT.theme);
        while (nodeIterator.hasNext()) {
            RDFNode rdfNode = nodeIterator.nextNode();
            if (Arrays.stream(themes).noneMatch(s -> s.equals(rdfNode.toString()))) {
                Set<Statement> triples = cleanThemeGraphRecursively(model, dataSet, rdfNode);
                if (triples != null) toBeAdded.addAll(triples);
            }
        }
        model.add(toBeAdded);
    }

    private Set<Statement> cleanThemeGraphRecursively(Model model, Resource dataSet, RDFNode node) {
        if (!node.isResource()) return null;
        Set<Statement> toBeAdded = new HashSet<>();
        model.remove(dataSet, DCAT.theme, node);
        List<Statement> statements = model.listStatements(new SimpleSelector(node.asResource(), null, (RDFNode) null)).toList();
        statements.forEach(statement -> {
            RDFNode object = statement.getObject();
            if (Arrays.stream(themes).anyMatch(s -> s.equals(object.toString())))
                toBeAdded.add(new StatementImpl(dataSet, DCAT.theme, object));
            else {
                if (model.contains(statement)) { // to prevent infinite loop if there is a loop in the graph
                    model.remove(statement.getSubject(), statement.getPredicate(), object);
                    cleanThemeGraphRecursively(model, dataSet, object);
                }
            }
        });
        return toBeAdded;
    }

}
