package com.ourstory.oscobblemon.neoforge;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import net.neoforged.fml.common.Mod;

@Mod(OSCobblemonLibrary.MOD_ID)
public final class OSCobblemonLibraryNeoForge {
    public OSCobblemonLibraryNeoForge() {
        OSCobblemonLibrary.init();
    }
}
