package org.mae.twg.backend.services;

public enum ConfigEnum {
    REFRESH_EXPIRATION_KEY,
    ACCESS_EXPIRATION_KEY;
    //TODO: курсы валют
    @Override
    public String toString() {
        switch (this) {
            case REFRESH_EXPIRATION_KEY -> {return "refresh_token_expiration";}
            case ACCESS_EXPIRATION_KEY -> {return "access_token_expiration";}
        }
        return "";
    }

}
