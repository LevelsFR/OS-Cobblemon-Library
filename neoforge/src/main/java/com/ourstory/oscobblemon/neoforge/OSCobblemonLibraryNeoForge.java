package com.ourstory.oscobblemon.neoforge;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import com.ourstory.oscobblemon.entity.PokemonEntities;
import com.ourstory.oscobblemon.internal.SpawnModifierRuntime;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(OSCobblemonLibrary.MOD_ID)
public final class OSCobblemonLibraryNeoForge {
    public OSCobblemonLibraryNeoForge() {
        OSCobblemonLibrary.init();
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            PokemonEntities.clearNearestPlayerDistanceCache();
            SpawnModifierRuntime.serverStopped(event.getServer());
        });
    }
}
