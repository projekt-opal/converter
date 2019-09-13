package org.diceresearch.webservices.web.control;

import org.diceresearch.webservices.model.dto.DataSetLongViewDTO;
import org.diceresearch.webservices.model.dto.FilterDTO;
import org.diceresearch.webservices.model.dto.ReceivingFilterDTO;
import org.diceresearch.webservices.utility.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestAPIController {

    private static final Logger logger = LoggerFactory.getLogger(RestAPIController.class);

    private final DataProvider provider;

    @Autowired
    public RestAPIController(DataProvider provider) {
        this.provider = provider;
    }

    @CrossOrigin
    @PostMapping("/dataSets/getNumberOfDataSets")
    public Long getNumberOFDataSets(
            @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(name = "searchIn", required = false) String[] searchIn,
            @RequestParam(name = "orderBy", required = false) String orderBy, // TODO: 26.02.19 if quality metrics can be set then we need to have asc, des
            @RequestBody(required = false) ReceivingFilterDTO[] filters
    ) {
        return provider.getNumberOfDatasets(searchQuery, searchIn, orderBy, filters);
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
        return provider.getSubListOFDataSets(searchQuery, low, limit, searchIn, orderBy, filters);
    }

    @CrossOrigin
    @GetMapping("/filters/list")
    public List<FilterDTO> getFilters() {
        return provider.getFilters();
    }
}
