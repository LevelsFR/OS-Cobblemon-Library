package com.ourstory.oscobblemon.battle;

/** Standard Showdown field weather that may be selected as a battle baseline. */
public enum InitialBattleWeather {
    RAIN("raindance"),
    SUN("sunnyday"),
    SANDSTORM("sandstorm"),
    SNOW("snow"),
    HARSH_SUN("desolateland"),
    HEAVY_RAIN("primordialsea"),
    STRONG_WINDS("deltastream");

    private final String showdownId;

    InitialBattleWeather(String showdownId) {
        this.showdownId = showdownId;
    }

    /** Returns the normalized Showdown condition ID used by the initializer. */
    public String showdownId() {
        return showdownId;
    }
}
