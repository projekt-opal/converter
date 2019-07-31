package org.diceresearch.dataseturlfetcher.utility;

import org.diceresearch.dataseturlfetcher.model.Portal;
import org.diceresearch.dataseturlfetcher.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSetFetcherPool {
    private final ApplicationContext context;
    private Map<String, DataSetFetcher> fetcherMap = new ConcurrentHashMap<>();
    private PortalRepository portalRepository;

    @Autowired
    public DataSetFetcherPool(ApplicationContext context, PortalRepository portalRepository) {
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

    @Autowired
    private Environment environment;

    public DataSetFetcher getFetcher(String portalName) {
        if (!fetcherMap.containsKey(portalName)) {
            System.out.println(environment.getProperty("info.crawler.tripleStore.url"));
            DataSetFetcher dataSetFetcher = context.getBean(DataSetFetcher.class);
            dataSetFetcher.setPortalName(portalName);
            fetcherMap.put(portalName, dataSetFetcher);
        }
        return fetcherMap.get(portalName);
    }
}
