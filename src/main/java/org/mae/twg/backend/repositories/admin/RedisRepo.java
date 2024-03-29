package org.mae.twg.backend.repositories.admin;

import java.util.Map;

public interface RedisRepo<Key, Value> {
    Map<Key, Value> findAll();
    void add(Key key, Value value);
    void delete(Key key);
    Value find(Key key);
    boolean exists(Key key);
}
