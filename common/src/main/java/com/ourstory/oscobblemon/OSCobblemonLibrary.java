package com.ourstory.oscobblemon;

/**
 * Shared entry point for OS Cobblemon Library.
 *
 * <p>The library must stay focused on reusable Cobblemon integration used by
 * Our Story mods. Loader-specific code belongs in the Fabric or NeoForge modules.</p>
 */
public final class OSCobblemonLibrary {
    public static final String MOD_ID = "os_cobblemon_library";
    public static final String NAME = "OS Cobblemon Library";

    private static boolean initialized;

    private OSCobblemonLibrary() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
