package com.ourstory.oscobblemon.internal;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.ourstory.oscobblemon.battle.InitialBattleWeather;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Pending pre-battle requests; weak keys let canceled battles be collected. */
public final class BattleWeatherRequests {
    private static final Map<PokemonBattle, InitialBattleWeather> PENDING =
            Collections.synchronizedMap(new WeakHashMap<>());

    private BattleWeatherRequests() {
    }

    public static void queue(PokemonBattle battle, InitialBattleWeather weather) {
        PENDING.put(battle, weather);
    }

    public static String consume(UUID battleId) {
        synchronized (PENDING) {
            Iterator<Map.Entry<PokemonBattle, InitialBattleWeather>> iterator =
                    PENDING.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<PokemonBattle, InitialBattleWeather> entry = iterator.next();
                if (entry.getKey().getBattleId().equals(battleId)) {
                    iterator.remove();
                    return entry.getValue().showdownId();
                }
            }
        }
        return null;
    }
}
