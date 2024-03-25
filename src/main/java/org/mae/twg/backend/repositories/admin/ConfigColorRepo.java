package org.mae.twg.backend.repositories.admin;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ConfigColorRepo implements RedisRepo<String, String> {
    private static final String KEY = "DynamicColorConfig";
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;
    public ConfigColorRepo(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }
    @Override
    public Map<String, String> findAll() {
        return hashOperations.entries(KEY);
    }

    @Override
    public void add(String key, String value) {
        hashOperations.put(KEY, key, value);
    }
    @Override
    public void delete(String key) {
        hashOperations.delete(KEY, key);
    }
    @Override
    public String find(String key) {
        return hashOperations.get(KEY, key);
    }
    @Override
    public boolean exists(String key) {
        return hashOperations.hasKey(KEY, key);
    }
}
