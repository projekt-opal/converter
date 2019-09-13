package org.diceresearch.webservices.model.mapper;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.diceresearch.webservices.model.dto.DataSetLongViewDTO;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mapper(componentModel = "spring")
public abstract class ModelToLongViewDTOMapper {
    private static final Logger logger = LoggerFactory.getLogger(ModelToLongViewDTOMapper.class);

    public DataSetLongViewDTO toDataSetLongViewDTO(Model model, Resource catalog) {
        try {
            String uri = getUri(model);
            String title = getTitle(model);
            String description = getDescription(model);
            String theme = getTheme(model);
            String fileType = "PDF";
            String issueDate = "2018-12-05";
            Random r = new Random();
            String overAllRating = Double.toString(r.nextDouble() * 4 + 1);
            List<String> keywords = Arrays.asList("key1", "key2");
            return new DataSetLongViewDTO()
                    .setUri(uri == null ? title : uri)
                    .setTitle(title)
                    .setDescription(description)
                    .setTheme(theme)
                    .setIssueDate(issueDate)
                    .setKeywords(keywords)
                    .setFileType(fileType)
                    .setOverallRating(overAllRating)
                    .setCatalog(catalog.getURI());
        } catch (Exception e) {
            logger.error("Error in ModelToLongViewDTOMapper ", e);
        }
        return null;
    }

    private String getUri(Model model) {
        ResIterator resIterator = model.listResourcesWithProperty(RDF.type, DCAT.Dataset);
        if (resIterator.hasNext()) return resIterator.nextResource().getURI();
        return null;
    }

    private String getTitle(Model model) {
        NodeIterator iterator = model.listObjectsOfProperty(DCTerms.title);
        if (!iterator.hasNext()) return "";// TODO: 27.02.19 What exactly we should return?
        RDFNode rdfNode = iterator.nextNode();//must exist
        return rdfNode.asLiteral().getString();
    }

    private String getDescription(Model model) {
        NodeIterator iterator = model.listObjectsOfProperty(DCTerms.description);
        if (!iterator.hasNext()) return "";// TODO: 27.02.19 What exactly we should return?
        RDFNode rdfNode = iterator.nextNode();//must exist
        return rdfNode.asLiteral().getString();
    }

    private String getTheme(Model model) {
        NodeIterator iterator = model.listObjectsOfProperty(DCAT.theme);
        if (!iterator.hasNext()) return "";// TODO: 27.02.19 What exactly we should return?
        RDFNode rdfNode = iterator.nextNode();//must exist
        if (rdfNode.isLiteral()) return rdfNode.asLiteral().getString();
        if (rdfNode.asResource().getURI().startsWith("http://projeckt-opal.de/theme/mcloud")) {
            NodeIterator nodeIterator = model.listObjectsOfProperty(rdfNode.asResource(), RDFS.label);
            if (nodeIterator.hasNext()) nodeIterator.nextNode().asLiteral().getString();
            //else go to next line return URI
        }
        return rdfNode.asResource().getURI();
    }
}
