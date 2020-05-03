package org.diceresearch.datasetfetcher.db;

import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.model.WorkingStatus;
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
            portalRepository.save(Portal.builder().name("mcloud").lastNotFetched(0).high(-1)
                    .queryAddress(tripleStoreURL+"mcloud/query")
                    .username(tripleStoreUsername).password(tripleStorePassword)
                    .outputQueue("dataset-graph").workingStatus(WorkingStatus.IDLE).step(100).build());
            portalRepository.save(Portal.builder().name("govdata").lastNotFetched(0).high(-1)
                    .queryAddress(tripleStoreURL+"govdata/query")
                    .username(tripleStoreUsername).password(tripleStorePassword)
                    .outputQueue("dataset-graph").workingStatus(WorkingStatus.IDLE).step(100).build());
            portalRepository.save(Portal.builder().name("europeandataportal").lastNotFetched(0).high(-1)
                    .queryAddress(tripleStoreURL+"europeandataportal/query")
                    .username(tripleStoreUsername).password(tripleStorePassword)
                    .outputQueue("dataset-graph").workingStatus(WorkingStatus.IDLE).step(100).build());
        }
    }
}
