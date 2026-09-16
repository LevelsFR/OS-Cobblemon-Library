package com.ourstory.oscobblemon.fabric;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import net.fabricmc.api.ModInitializer;

public final class OSCobblemonLibraryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OSCobblemonLibrary.init();
    }
}
