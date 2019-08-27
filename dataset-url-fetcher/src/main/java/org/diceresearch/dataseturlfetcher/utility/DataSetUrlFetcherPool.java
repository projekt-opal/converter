package org.diceresearch.dataseturlfetcher.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataSetUrlFetcherPool {
    private final ApplicationContext context;

    private Map<Integer, DataSetUrlFetcher> pool = new HashMap<>();

    @Autowired
    public DataSetUrlFetcherPool(ApplicationContext context) {
        this.context = context;
    }

    public DataSetUrlFetcher getFetcher(Integer id) {
        if (!pool.containsKey(id)) {
            DataSetUrlFetcher fetcher = context.getBean(DataSetUrlFetcher.class);
            fetcher.initialQueryExecutionFactory(id);
            fetcher.setCanceled(false);
            pool.put(id, fetcher);
        }
        return pool.get(id);
    }
}
