package org.mae.twg.backend.models.travel.enums;

public enum Stars {
    NULL,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE;

    @Override
    public String toString() {
        switch (this) {
            case NULL -> {return "Без звезд";}
            case ONE -> {return "⭐️";}
            case TWO -> {return "⭐️⭐️";}
            case THREE -> {return "⭐️⭐️⭐️";}
            case FOUR -> {return "⭐️⭐️⭐️⭐️";}
            case FIVE -> {return "⭐️⭐️⭐️⭐️⭐️";}
        }
        return "";
    }
}