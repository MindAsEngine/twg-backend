package org.mae.twg.backend.repositories;

import org.mae.twg.backend.models.ConfigParam;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends KeyValueRepository<ConfigParam, String> {
}
