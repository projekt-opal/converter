package org.diceresearch.dataseturlfetcher.repository;

import org.diceresearch.dataseturlfetcher.model.Portal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends CrudRepository<Portal, Integer> {
    Portal findByName(String portalName);
}
