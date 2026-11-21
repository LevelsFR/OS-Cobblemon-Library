package com.ourstory.oscobblemon.entity;

import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PokemonEntitiesTest {
    @Test
    void clearNearestPlayerDistanceCacheDropsEntriesAndResetsTick() throws ReflectiveOperationException {
        Field cacheField = PokemonEntities.class.getDeclaredField("NEAREST_PLAYER_DISTANCE_SQUARED");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Reference2DoubleOpenHashMap<Object> cache =
                (Reference2DoubleOpenHashMap<Object>) cacheField.get(null);
        cache.put(new Object(), 16.0);
        cache.put(new Object(), 64.0);

        Field tickField = PokemonEntities.class.getDeclaredField("distanceCacheTick");
        tickField.setAccessible(true);
        tickField.setInt(null, 42);

        assertEquals(2, cache.size());
        PokemonEntities.clearNearestPlayerDistanceCache();

        assertTrue(cache.isEmpty());
        assertEquals(Integer.MIN_VALUE, tickField.getInt(null));
    }
}
