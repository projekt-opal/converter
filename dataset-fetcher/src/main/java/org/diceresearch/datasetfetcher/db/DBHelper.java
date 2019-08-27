package org.diceresearch.datasetfetcher.db;

import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DBHelper {

    private final PortalRepository portalRepository;

    @Value("${CRAWLER_TRIPLESTORE_URL}")
    private String tripleStoreURL;

    @Value("${CRAWLER_TRIPLESTORE_USERNAME}")
    private String tripleStoreUsername;

    @Value("${CRAWLER_TRIPLESTORE_PASSWORD}")
    private String tripleStorePassword;

    @Autowired
    public DBHelper(PortalRepository portalRepository) {
        this.portalRepository = portalRepository;
    }

    @PostConstruct
    public void initDB() {
        Iterable<Portal> portals = portalRepository.findAll();
        if (!portals.iterator().hasNext()) {
            portalRepository.save(new Portal().setName("mcloud").setLastNotFetched(0).setHigh(-1)
                    .setQueryAddress(tripleStoreURL+"/mcloud/query")
                    .setUsername(tripleStoreUsername).setPassword(tripleStorePassword)
                    .setOutputQueue("dataset-graph"));
            portalRepository.save(new Portal().setName("govdata").setLastNotFetched(0).setHigh(-1)
                    .setQueryAddress(tripleStoreURL+"/govdata/query")
                    .setUsername(tripleStoreUsername).setPassword(tripleStorePassword)
                    .setOutputQueue("dataset-graph"));
            portalRepository.save(new Portal().setName("europeandataportal").setLastNotFetched(0).setHigh(-1)
                    .setQueryAddress(tripleStoreURL+"/europeandataportal/query")
                    .setUsername(tripleStoreUsername).setPassword(tripleStorePassword)
                    .setOutputQueue("dataset-graph"));
        }
    }
}
