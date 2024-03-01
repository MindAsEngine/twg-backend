package org.mae.twg.backend.models;

import java.util.List;

public interface Model {
    Long getId();
    <T extends Model> List<? extends Local<T>> getLocalizations();
}
