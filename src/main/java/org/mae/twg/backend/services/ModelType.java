package org.mae.twg.backend.services;

public enum ModelType {
    HOTEL,
    HOSPITAL,
    COUNTRY,
    SIGHT,
    TOUR,
    NEWS,
    USER;

    @Override
    public String toString() {
        switch (this) {
            case HOTEL -> {return "hotel";}
            case HOSPITAL -> {return "hospital";}
            case COUNTRY -> {return "country";}
            case SIGHT -> {return "sight";}
            case TOUR -> {return "tour";}
            case NEWS -> {return "news";}
            case USER -> {return "user";}
        }
        return "";
    }
}
