package com.ourstory.oscobblemon.internal;

/** Runtime state for the Cobblemon-version-specific Showdown bridge. */
public final class BattleWeatherHookStatus {
    private static volatile boolean supportedVersion;
    private static volatile boolean bootPatchInstalled;

    private BattleWeatherHookStatus() {
    }

    public static void configure(boolean supported) {
        supportedVersion = supported;
        bootPatchInstalled = false;
    }

    public static boolean isSupportedVersion() {
        return supportedVersion;
    }

    public static boolean isAvailable() {
        return supportedVersion && bootPatchInstalled;
    }

    public static void markBootPatchInstalled(boolean installed) {
        bootPatchInstalled = supportedVersion && installed;
    }
}
