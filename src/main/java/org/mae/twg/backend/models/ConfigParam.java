package org.mae.twg.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@RedisHash("dynamic_config")
public class ConfigParam {
    @Id
    private String key;
    private String value;
}
