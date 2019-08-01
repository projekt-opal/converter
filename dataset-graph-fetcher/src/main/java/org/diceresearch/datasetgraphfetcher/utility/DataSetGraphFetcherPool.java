package org.diceresearch.datasetgraphfetcher.utility;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSetGraphFetcherPool {
    private final ApplicationContext context;
    private Map<String, DataSetGraphFetcher> fetcherMap = new ConcurrentHashMap<>();

    @Autowired
    public DataSetGraphFetcherPool(ApplicationContext context) {
        this.context = context;
    }

    public DataSetGraphFetcher getFetcher(String portalName) {
        if (!fetcherMap.containsKey(portalName)) {
            DataSetGraphFetcher dataSetGraphFetcher = context.getBean(DataSetGraphFetcher.class);
            dataSetGraphFetcher.initialQueryExecutionFactory(portalName);
            fetcherMap.put(portalName, dataSetGraphFetcher);
        }
        return fetcherMap.get(portalName);
    }
}
