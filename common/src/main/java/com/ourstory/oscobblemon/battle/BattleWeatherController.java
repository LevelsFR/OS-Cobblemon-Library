package com.ourstory.oscobblemon.battle;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.ourstory.oscobblemon.internal.BattleWeatherHookStatus;

import java.util.Objects;

/** Version-gated bridge for synchronizing world-derived weather with a battle. */
public final class BattleWeatherController {
    private BattleWeatherController() {
    }

    /** Returns whether the Cobblemon-version-specific bridge is installed. */
    public static boolean isAvailable() {
        return BattleWeatherHookStatus.isAvailable();
    }

    /**
     * Queues one world-weather synchronization attempt for an active battle.
     * A battle weather set by a move or ability takes priority; callers should
     * retry the latest ambient state after Cobblemon reports that weather has
     * cleared.
     *
     * <p>Call on the Minecraft server thread. The return value only indicates
     * that the request was queued, not that the simulator applied it.</p>
     *
     * @return {@code true} when queued, or {@code false} if the hook is
     *         unavailable or the battle has not started or has ended
     */
    public static boolean queueAmbientWeather(
            PokemonBattle battle,
            AmbientBattleWeather weather
    ) {
        Objects.requireNonNull(battle, "battle");
        Objects.requireNonNull(weather, "weather");
        if (!isAvailable() || !battle.getStarted() || battle.getEnded()) {
            return false;
        }
        battle.writeShowdownAction(">osclweather " + weather.showdownId());
        return true;
    }
}
