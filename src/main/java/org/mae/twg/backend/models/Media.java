package org.mae.twg.backend.models;

public interface Media<T extends Model> {
    T getModel();
    String getMediaPath();
}
