package org.mae.twg.backend.models;

import org.mae.twg.backend.models.travel.enums.Localization;

public interface Local<T extends Model> {
    String getString();
    Localization getLocalization();
    T getModel();
}
