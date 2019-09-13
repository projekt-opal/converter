package org.diceresearch.webservices.utility;

import org.diceresearch.webservices.model.dto.DataSetLongViewDTO;
import org.diceresearch.webservices.model.dto.FilterDTO;
import org.diceresearch.webservices.model.dto.ReceivingFilterDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DataProvider {

    long getNumberOfDatasets(String searchQuery, String[] searchIn, String orderBy, ReceivingFilterDTO[] filters);

    List<DataSetLongViewDTO> getSubListOFDataSets(String searchQuery, Long low, Long limit, String[] searchIn, String orderBy, ReceivingFilterDTO[] filters);

    List<FilterDTO> getFilters();
}
