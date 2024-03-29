package org.mae.twg.backend.dto;

import org.mae.twg.backend.models.travel.enums.Localization;

import java.io.Serializable;

public interface ModelDTO extends Serializable {
    Long getId();
    Localization getLocalization();
}
