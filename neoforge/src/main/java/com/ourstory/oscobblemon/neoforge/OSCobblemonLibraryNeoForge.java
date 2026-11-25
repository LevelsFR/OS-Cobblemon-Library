package com.ourstory.oscobblemon.neoforge;

import com.ourstory.oscobblemon.OSCobblemonLibrary;
import com.ourstory.oscobblemon.entity.PokemonEntities;
import com.ourstory.oscobblemon.internal.SpawnModifierRuntime;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(OSCobblemonLibrary.MOD_ID)
public final class OSCobblemonLibraryNeoForge {
    public OSCobblemonLibraryNeoForge() {
        OSCobblemonLibrary.init();
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                event.getDispatcher().register(Commands.literal("oscl").executes(context -> {
                    String version = ModList.get()
                            .getModContainerById(OSCobblemonLibrary.MOD_ID)
                            .map(container -> container.getModInfo().getVersion().toString())
                            .orElse("unknown");
                    context.getSource().sendSuccess(
                            () -> Component.literal(OSCobblemonLibrary.NAME + " v" + version),
                            false
                    );
                    return 1;
                }))
        );
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) -> {
            PokemonEntities.clearNearestPlayerDistanceCache();
            SpawnModifierRuntime.serverStopped(event.getServer());
        });
    }
}
