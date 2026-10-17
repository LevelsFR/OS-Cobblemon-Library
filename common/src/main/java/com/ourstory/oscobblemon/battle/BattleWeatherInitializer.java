package com.ourstory.oscobblemon.battle;

import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.ourstory.oscobblemon.internal.BattleWeatherHookStatus;
import com.ourstory.oscobblemon.internal.BattleWeatherRequests;

import java.util.Objects;

/**
 * Sets a one-time initial Showdown field weather before Cobblemon starts the
 * battle. The compatibility hook currently targets Cobblemon 1.8.2 only.
 */
public final class BattleWeatherInitializer {
    private BattleWeatherInitializer() {
    }

    /** Returns whether the verified runtime hook is installed and ready. */
    public static boolean isAvailable() {
        return BattleWeatherHookStatus.isAvailable();
    }

    /**
     * Queues an initial field weather for this pre-start event. Call from a
     * {@link BattleStartedEvent.Pre} subscriber. The simulator remains
     * authoritative after startup.
     *
     * @return {@code true} when queued, or {@code false} when the runtime hook
     *         is unavailable for the installed Cobblemon version
     */
    public static boolean setInitialWeather(
            BattleStartedEvent.Pre event,
            InitialBattleWeather weather
    ) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(weather, "weather");
        if (!isAvailable()) {
            return false;
        }
        BattleWeatherRequests.queue(event.getBattle(), weather);
        return true;
    }
}
