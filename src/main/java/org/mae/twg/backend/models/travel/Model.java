package org.mae.twg.backend.models.travel;

import org.mae.twg.backend.models.travel.localization.Local;

import java.util.List;

public interface Model {
    Long getId();
    List<? extends Local> getLocalizations();
}
