package org.diceresearch.dataseturlfetcher.utility;

import org.diceresearch.dataseturlfetcher.model.Portal;
import org.diceresearch.dataseturlfetcher.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSetUrlFetcherPool {
    private final ApplicationContext context;
    private Map<String, DataSetUrlFetcher> fetcherMap = new ConcurrentHashMap<>();
    private PortalRepository portalRepository;

    @Autowired
    public DataSetUrlFetcherPool(ApplicationContext context, PortalRepository portalRepository) {
        this.context = context;
        this.portalRepository = portalRepository;
    }

    @PostConstruct
    public void initDB() {
        Iterable<Portal> portals = portalRepository.findAll();
        if (!portals.iterator().hasNext()) {
            portalRepository.save(new Portal().setName("mcloud").setLastNotFetched(0).setHigh(-1));
            portalRepository.save(new Portal().setName("govdata").setLastNotFetched(0).setHigh(-1));
            portalRepository.save(new Portal().setName("europeandataportal").setLastNotFetched(0).setHigh(-1));
        }
    }

    public DataSetUrlFetcher getFetcher(String portalName) {
        if (!fetcherMap.containsKey(portalName)) {
            DataSetUrlFetcher dataSetUrlFetcher = context.getBean(DataSetUrlFetcher.class);
            dataSetUrlFetcher.setPortalName(portalName);
            fetcherMap.put(portalName, dataSetUrlFetcher);
        }
        return fetcherMap.get(portalName);
    }
}
