package com.ourstory.oscobblemon.internal.mixin;

import com.ourstory.oscobblemon.internal.BattleWeatherHookStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BattleWeatherMixinPluginTest {
    @Test
    void unavailableLoadersDisableTheHookWithoutFailingMixinStartup() {
        BattleWeatherHookStatus.configure(true);
        BattleWeatherHookStatus.markBootPatchInstalled(true);
        BattleWeatherMixinPlugin plugin = new BattleWeatherMixinPlugin();

        assertDoesNotThrow(() -> plugin.onLoad("com.ourstory.oscobblemon.internal.mixin"));
        assertFalse(BattleWeatherHookStatus.isSupportedVersion());
        assertFalse(BattleWeatherHookStatus.isAvailable());
        assertFalse(plugin.shouldApplyMixin("example.Target", "example.Mixin"));
    }
}
