package org.diceresearch.webservices.utility.elasticsearch;

import org.diceresearch.webservices.model.dto.DataSetLongViewDTO;
import org.diceresearch.webservices.model.dto.FilterDTO;
import org.diceresearch.webservices.model.dto.ReceivingFilterDTO;
import org.diceresearch.webservices.utility.DataProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("elasticsearch")
@Component
public class ElasticSearchProvider implements DataProvider {
    @Override
    public long getNumberOfDatasets(String searchQuery, String[] searchIn, String orderBy, ReceivingFilterDTO[] filters) {
        return 0; //Todo complete it
    }

    @Override
    public List<DataSetLongViewDTO> getSubListOFDataSets(String searchQuery, Long low, Long limit, String[] searchIn, String orderBy, ReceivingFilterDTO[] filters) {
        return null; //Todo complete it
    }

    @Override
    public List<FilterDTO> getFilters() {
        return null; //Todo complete it
    }
}
