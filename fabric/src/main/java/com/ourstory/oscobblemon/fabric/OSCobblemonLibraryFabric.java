package com.ourstory.oscobblemon.fabric;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import com.ourstory.oscobblemon.entity.PokemonEntities;
import com.ourstory.oscobblemon.internal.SpawnModifierRuntime;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class OSCobblemonLibraryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        OSCobblemonLibrary.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("oscl").executes(context -> {
                    String version = FabricLoader.getInstance()
                            .getModContainer(OSCobblemonLibrary.MOD_ID)
                            .map(container -> container.getMetadata().getVersion().getFriendlyString())
                            .orElse("unknown");
                    context.getSource().sendSuccess(
                            () -> Component.literal(OSCobblemonLibrary.NAME + " v" + version),
                            false
                    );
                    return 1;
                }))
        );
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            PokemonEntities.clearNearestPlayerDistanceCache();
            SpawnModifierRuntime.serverStopped(server);
        });
    }
}
