package org.diceresearch.webservices.web.control.triplestore;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.diceresearch.webservices.model.dto.DataSetLongViewDTO;
import org.diceresearch.webservices.model.dto.FilterDTO;
import org.diceresearch.webservices.model.dto.FilterValueDTO;
import org.diceresearch.webservices.model.dto.ReceivingFilterDTO;
import org.diceresearch.webservices.model.mapper.ModelToLongViewDTOMapper;
import org.diceresearch.webservices.utility.SparQLRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class RestAPIController {

    private static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final SparQLRunner sparQLRunner;
    private final ModelToLongViewDTOMapper modelToLongViewDTOMapper;


    @Autowired
    public RestAPIController(SparQLRunner sparQLRunner, ModelToLongViewDTOMapper modelToLongViewDTOMapper) {
        this.sparQLRunner = sparQLRunner;
        this.modelToLongViewDTOMapper = modelToLongViewDTOMapper;
    }

    @CrossOrigin
    @PostMapping("/dataSets/getNumberOfDataSets")
    public Long getNumberOFDataSets(
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "searchIn", required = false) String[] searchIn,
            @RequestParam(name = "orderBy", required = false) String orderBy, // TODO: 26.02.19 if quality metrics can be set then we need to have asc, des
            @RequestBody(required = false) ReceivingFilterDTO[] filters
    ) {

        Long num = -1L;
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString();

            String filtersString = getFiltersString(searchQuery, filters);

            String query = "SELECT (COUNT(DISTINCT ?s) AS ?num) WHERE {  GRAPH ?g { " +
                    "?s a dcat:Dataset. " + filtersString +
                    "} }";

            pss.setCommandText(query);
            pss.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");

            num = sparQLRunner.execSelectCount(pss.asQuery());
        } catch (Exception e) {
            logger.error("An error occurred in getting the results", e);
        }
        return num;
    }

    @CrossOrigin
    @PostMapping("/dataSets/getSubList")
    public List<DataSetLongViewDTO> getSubListOFDataSets(
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "searchIn", required = false) String[] searchIn,
            @RequestParam(name = "orderBy", required = false) String orderBy, // TODO: 26.02.19 if quality metrics can be set then we need to have asc, des
            @RequestParam(name = "low", required = false, defaultValue = "0") Long low,
            @RequestParam(name = "limit", required = false, defaultValue = "10") Long limit,
            @RequestBody(required = false) ReceivingFilterDTO[] filters
    ) {
        List<DataSetLongViewDTO> ret = new ArrayList<>();
        try {
            ParameterizedSparqlString pss = new ParameterizedSparqlString();

            String filtersString = getFiltersString(searchQuery, filters);

            String query = "SELECT DISTINCT ?s WHERE { GRAPH ?g { " +
                    "?s a dcat:Dataset. " + filtersString +
                    "} } OFFSET " + low + " LIMIT " + limit;

            pss.setCommandText(query);
            pss.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");

            List<Resource> dataSets = sparQLRunner.execSelect(pss.asQuery(), "s");

            dataSets.forEach(dataSet -> {
                Model model = getGraphOfDataSet(dataSet);
                Resource catalog = getCatalog(dataSet);
                DataSetLongViewDTO dataSetLongViewDTO = modelToLongViewDTOMapper.toDataSetLongViewDTO(model, catalog);
                ret.add(dataSetLongViewDTO);
            });

        } catch (Exception e) {
            logger.error("An error occurred in getting the results", e);
        }
        return ret;
    }

    private String getFiltersString(String searchQuery, ReceivingFilterDTO[] filters) {
        StringBuilder filtersString = new StringBuilder();
        if (searchQuery.length() > 0)
            filtersString.append("?s ?p ?o. +\n" + "FILTER(isLiteral(?o)).  +\n" + "FILTER CONTAINS (STR(?o),\" + searchQuery + \").");
        if ((filters != null && filters.length > 0)) {
            for (ReceivingFilterDTO entry : filters) {
                String key = entry.getProperty();
                String[] values = entry.getValues();
                if (values.length == 1) {
                    if (values[0].startsWith("http://"))
                        filtersString.append(" ?s <").append(key).append("> <").append(values[0]).append("> .");
                    else
                        filtersString.append(" ?s <").append(key).append("> \"").append(values[0]).append("\" .");
                } else if (values.length > 1) {
                    filtersString.append("VALUES (?value) {  ");
                    for (String val : values)
                        if (val.startsWith("http://"))
                            filtersString.append(" ( <").append(val).append("> )");
                        else
                            filtersString.append(" ( \"").append(val).append("\" )");
                    filtersString.append("} ?s <").append(key).append("> ?value.");
                }
            }
        }
        return filtersString.toString();
    }

    @CrossOrigin
    @GetMapping("/filters/list")
    public List<FilterDTO> getFilters() {
        List<FilterDTO> ret = new ArrayList<>();
        ret.add(new FilterDTO()
                .setUri("http://www.w3.org/ns/dcat#theme")
                .setTitle("Theme")
                .setValues(Arrays.asList(
                        new FilterValueDTO("Energy", "Energy", 10),
                        new FilterValueDTO("Environment", "Environment", 143),
                        new FilterValueDTO("http://projeckt-opal.de/theme/mcloud/climateAndWeather", "climate and weather", 143))));
        ret.add(new FilterDTO()
                .setUri("http://www.w3.org/ns/dcat#publisher")
                .setTitle("publisher")
                .setValues(Arrays.asList(
                        new FilterValueDTO("DB", "DB", 10),
                        new FilterValueDTO("others", "others", 143))));
        ret.add(new FilterDTO()
                .setUri("http://purl.org/dc/terms/license")
                .setTitle("license")
                .setValues(Arrays.asList(
                        new FilterValueDTO("CCv4.0", "CCv4.0", 50),
                        new FilterValueDTO("others", "others", 93))));
        return ret;
    }

    private Model getGraphOfDataSet(Resource dataSet) {
        Model model;


        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "CONSTRUCT { " + "?dataSet ?predicate ?object. " +
                "?object ?p2 ?o2} " +
                "WHERE { " +
                "  GRAPH ?g { " +
                "    ?dataSet ?predicate ?object. " +
                "    OPTIONAL { ?object ?p2 ?o2 } " +
                "  }" +
                "}");

        pss.setParam("dataSet", dataSet);

        model = sparQLRunner.executeConstruct(pss.asQuery());

        return model;
    }

    private Resource getCatalog(Resource dataSet) {
        ParameterizedSparqlString pss = new ParameterizedSparqlString("" +
                "SELECT ?catalog " +
                "WHERE { " +
                "  GRAPH ?g { " +
                "    ?catalog dcat:dataset ?dataSet" +
                "  }" +
                "}");

        pss.setParam("dataSet", dataSet);
        pss.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");

        List<Resource> resources;
        try {
            resources = sparQLRunner.execSelect(pss.asQuery(), "catalog");
        } catch (Exception e) {
            logger.error("An error occurred in getting the catalog {} {}", dataSet, e);
            return null;
        }
        if (resources.size() != 1) {
            logger.error("catalog size is not 1, {}", dataSet);
            return null;
        }
        return resources.get(0);
    }

}