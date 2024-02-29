package org.mae.twg.backend.models;

import org.mae.twg.backend.models.travel.enums.Localization;

public interface Local {
    String getString();
    Localization getLocalization();

    Model getModel();
}
