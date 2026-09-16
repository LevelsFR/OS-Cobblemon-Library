package com.ourstory.oscobblemon.pokedex;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.cobblemon.mod.common.api.pokedex.CaughtPercent;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokedex.SeenCount;
import com.cobblemon.mod.common.api.pokedex.SeenPercent;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Read-only helpers for common Pokédex queries.
 *
 * <p>This class keeps repeated access to Cobblemon's player Pokédex manager and
 * species/form records in one place while preserving Cobblemon's own progress
 * model.</p>
 */
public final class PokedexQueries {
    private PokedexQueries() {
    }

    /**
     * Returns the player's Cobblemon Pokédex manager.
     */
    public static PokedexManager manager(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        return Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(player);
    }

    /**
     * Returns a player's Pokédex manager by UUID.
     */
    public static PokedexManager manager(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(playerId);
    }

    /**
     * Returns the highest Pokédex progress recorded for a species.
     */
    public static PokedexEntryProgress progress(ServerPlayer player, ResourceLocation speciesId) {
        Objects.requireNonNull(speciesId, "speciesId");
        return manager(player).getKnowledgeForSpecies(speciesId);
    }

    /**
     * Returns the highest Pokédex progress recorded for this Pokémon's species.
     */
    public static PokedexEntryProgress progress(ServerPlayer player, Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return progress(player, pokemon.getSpecies().getResourceIdentifier());
    }

    /**
     * Returns true once the species has been encountered in the Pokédex.
     */
    public static boolean hasSeen(ServerPlayer player, ResourceLocation speciesId) {
        return progress(player, speciesId) != PokedexEntryProgress.NONE;
    }

    /**
     * Returns true once this Pokémon's species has been encountered in the Pokédex.
     */
    public static boolean hasSeen(ServerPlayer player, Pokemon pokemon) {
        return progress(player, pokemon) != PokedexEntryProgress.NONE;
    }

    /**
     * Returns true once the species has been caught.
     */
    public static boolean hasCaught(ServerPlayer player, ResourceLocation speciesId) {
        return progress(player, speciesId) == PokedexEntryProgress.CAUGHT;
    }

    /**
     * Returns true once this Pokémon's species has been caught.
     */
    public static boolean hasCaught(ServerPlayer player, Pokemon pokemon) {
        return progress(player, pokemon) == PokedexEntryProgress.CAUGHT;
    }

    /**
     * Returns the progress for one species form.
     */
    public static PokedexEntryProgress formProgress(
            ServerPlayer player,
            ResourceLocation speciesId,
            String formName
    ) {
        Objects.requireNonNull(speciesId, "speciesId");
        Objects.requireNonNull(formName, "formName");

        SpeciesDexRecord speciesRecord = manager(player).getSpeciesRecord(speciesId);
        if (speciesRecord == null) {
            return PokedexEntryProgress.NONE;
        }

        FormDexRecord formRecord = speciesRecord.getFormRecord(formName);
        return formRecord == null ? PokedexEntryProgress.NONE : formRecord.getKnowledge();
    }

    /**
     * Returns the progress recorded for this Pokémon's current form.
     */
    public static PokedexEntryProgress formProgress(ServerPlayer player, Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");
        return formProgress(
                player,
                pokemon.getSpecies().getResourceIdentifier(),
                pokemon.getForm().getName()
        );
    }

    /**
     * Returns true once a specific species form has been encountered.
     */
    public static boolean hasSeenForm(
            ServerPlayer player,
            ResourceLocation speciesId,
            String formName
    ) {
        return formProgress(player, speciesId, formName) != PokedexEntryProgress.NONE;
    }

    /**
     * Returns true once this Pokémon's current form has been encountered.
     */
    public static boolean hasSeenForm(ServerPlayer player, Pokemon pokemon) {
        return formProgress(player, pokemon) != PokedexEntryProgress.NONE;
    }

    /**
     * Returns true once a specific species form has been caught.
     */
    public static boolean hasCaughtForm(
            ServerPlayer player,
            ResourceLocation speciesId,
            String formName
    ) {
        return formProgress(player, speciesId, formName) == PokedexEntryProgress.CAUGHT;
    }

    /**
     * Returns true once this Pokémon's current form has been caught.
     */
    public static boolean hasCaughtForm(ServerPlayer player, Pokemon pokemon) {
        return formProgress(player, pokemon) == PokedexEntryProgress.CAUGHT;
    }

    /**
     * Returns an immutable copy of all aspects known for a species.
     */
    public static Set<String> seenAspects(ServerPlayer player, ResourceLocation speciesId) {
        Objects.requireNonNull(speciesId, "speciesId");

        SpeciesDexRecord record = manager(player).getSpeciesRecord(speciesId);
        return record == null ? Set.of() : Set.copyOf(record.getAspects());
    }

    /**
     * Returns whether the Pokédex has recorded an aspect for a species.
     */
    public static boolean hasSeenAspect(
            ServerPlayer player,
            ResourceLocation speciesId,
            String aspect
    ) {
        Objects.requireNonNull(aspect, "aspect");
        SpeciesDexRecord record = manager(player).getSpeciesRecord(speciesId);
        return record != null && record.hasAspect(aspect);
    }

    /**
     * Returns whether the Pokédex has recorded this Pokémon's current shiny state
     * for its current form.
     */
    public static boolean hasSeenShinyState(ServerPlayer player, Pokemon pokemon) {
        Objects.requireNonNull(pokemon, "pokemon");

        SpeciesDexRecord speciesRecord = manager(player)
                .getSpeciesRecord(pokemon.getSpecies().getResourceIdentifier());
        if (speciesRecord == null) {
            return false;
        }

        FormDexRecord formRecord = speciesRecord.getFormRecord(pokemon.getForm().getName());
        return formRecord != null && formRecord.hasSeenShinyState(pokemon.getShiny());
    }

    /**
     * Returns the global number of species encountered by the player.
     */
    public static int seenCount(ServerPlayer player) {
        return manager(player).getGlobalCalculatedValue(SeenCount.INSTANCE);
    }

    /**
     * Returns the global number of species caught by the player.
     */
    public static int caughtCount(ServerPlayer player) {
        return manager(player).getGlobalCalculatedValue(CaughtCount.INSTANCE);
    }

    /**
     * Returns the global percentage of Pokédex entries encountered.
     */
    public static float seenPercent(ServerPlayer player) {
        return manager(player).getGlobalCalculatedValue(SeenPercent.INSTANCE);
    }

    /**
     * Returns the global percentage of Pokédex entries caught.
     */
    public static float caughtPercent(ServerPlayer player) {
        return manager(player).getGlobalCalculatedValue(CaughtPercent.INSTANCE);
    }

    /**
     * Returns the number of encountered entries inside a specific Pokédex.
     */
    public static int seenCount(ServerPlayer player, ResourceLocation dexId) {
        Objects.requireNonNull(dexId, "dexId");
        return manager(player).getDexCalculatedValue(dexId, SeenCount.INSTANCE);
    }

    /**
     * Returns the number of caught entries inside a specific Pokédex.
     */
    public static int caughtCount(ServerPlayer player, ResourceLocation dexId) {
        Objects.requireNonNull(dexId, "dexId");
        return manager(player).getDexCalculatedValue(dexId, CaughtCount.INSTANCE);
    }

    /**
     * Returns the encountered percentage inside a specific Pokédex.
     */
    public static float seenPercent(ServerPlayer player, ResourceLocation dexId) {
        Objects.requireNonNull(dexId, "dexId");
        return manager(player).getDexCalculatedValue(dexId, SeenPercent.INSTANCE);
    }

    /**
     * Returns the caught percentage inside a specific Pokédex.
     */
    public static float caughtPercent(ServerPlayer player, ResourceLocation dexId) {
        Objects.requireNonNull(dexId, "dexId");
        return manager(player).getDexCalculatedValue(dexId, CaughtPercent.INSTANCE);
    }
}
