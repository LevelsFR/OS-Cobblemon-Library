package com.ourstory.oscobblemon.battle;

/** World-derived weather that may be synchronized into an active battle. */
public enum AmbientBattleWeather {
    CLEAR("none"),
    RAIN("raindance"),
    SNOW("snow"),
    SANDSTORM("sandstorm");

    private final String showdownId;

    AmbientBattleWeather(String showdownId) {
        this.showdownId = showdownId;
    }

    String showdownId() {
        return showdownId;
    }
}
