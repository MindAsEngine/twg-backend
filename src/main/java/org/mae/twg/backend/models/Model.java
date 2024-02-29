package org.mae.twg.backend.models;

import java.util.List;

public interface Model {
    Long getId();
    List<? extends Local> getLocalizations();
}
