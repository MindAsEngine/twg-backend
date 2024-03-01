package org.mae.twg.backend.dto;

import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.io.Serializable;

public interface ModelDTO<T extends Model> extends Serializable {
    Long getId();
    Localization getLocalization();
}
