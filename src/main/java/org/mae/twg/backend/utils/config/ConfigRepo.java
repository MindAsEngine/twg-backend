package org.mae.twg.backend.utils.config;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends KeyValueRepository<ConfigParam, String> {
}
