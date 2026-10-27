package com.ourstory.oscobblemon.fabric;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import com.ourstory.oscobblemon.entity.PokemonEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class OSCobblemonLibraryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OSCobblemonLibrary.init();
        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                PokemonEntities.clearNearestPlayerDistanceCache()
        );
    }
}
