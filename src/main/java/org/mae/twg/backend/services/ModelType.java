package org.mae.twg.backend.services;

public enum ModelType {
    HOTEL,
    COUNTRY,
    SIGHT,
    TOUR;

    @Override
    public String toString() {
        switch (this) {
            case HOTEL -> {return "hotel";}
            case COUNTRY -> {return "country";}
            case SIGHT -> {return "sight";}
            case TOUR -> {return "tour";}
        }
        return "";
    }
}
