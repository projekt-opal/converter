package org.diceresearch.datasetfetcher.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataSetFetcherPool {
    private final ApplicationContext context;

    private Map<Integer, DataSetFetcher> pool = new HashMap<>();

    @Autowired
    public DataSetFetcherPool(ApplicationContext context) {
        this.context = context;
    }

    public DataSetFetcher getFetcher(Integer id) {
        if (!pool.containsKey(id)) {
            DataSetFetcher fetcher = context.getBean(DataSetFetcher.class);
            pool.put(id, fetcher);
        }
        return pool.get(id);
    }
}
