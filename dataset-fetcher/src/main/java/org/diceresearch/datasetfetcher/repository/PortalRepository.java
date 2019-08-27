package org.diceresearch.datasetfetcher.repository;

import org.diceresearch.datasetfetcher.model.Portal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends CrudRepository<Portal, Integer> {
}
