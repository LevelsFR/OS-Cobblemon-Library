package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Optional;

/**
 * Shared helpers for creating and spawning Pokémon through Cobblemon's native
 * {@link PokemonProperties} API.
 *
 * <p>This avoids command execution for ordinary programmatic spawning and keeps
 * property parsing consistent across consuming mods.</p>
 */
public final class PokemonCreation {
    private PokemonCreation() {
    }

    /**
     * Parses Cobblemon's native Pokémon property syntax.
     */
    public static PokemonProperties properties(String query) {
        Objects.requireNonNull(query, "query");
        return PokemonProperties.Companion.parse(query.trim());
    }

    /**
     * Creates properties targeting one validated species.
     */
    public static PokemonProperties forSpecies(String speciesId) {
        Species species = PokemonSpeciesResolver.require(speciesId);
        return properties("species=" + species.getResourceIdentifier());
    }

    /**
     * Creates a Pokémon using Cobblemon's native property parser and rolls.
     */
    public static Pokemon create(String query) {
        return properties(query).create();
    }

    /**
     * Creates a Pokémon using a player as the Cobblemon roll context.
     */
    public static Pokemon create(String query, ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        return properties(query).create(player);
    }

    /**
     * Creates, but does not spawn, a Pokémon entity.
     */
    public static PokemonEntity createEntity(Level level, String query) {
        Objects.requireNonNull(level, "level");
        return properties(query).createEntity(level);
    }

    /**
     * Creates, but does not spawn, a Pokémon entity using a player as the roll
     * context.
     */
    public static PokemonEntity createEntity(Level level, String query, ServerPlayer player) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(player, "player");
        return properties(query).createEntity(level, player);
    }

    /**
     * Spawns a Pokémon from native Cobblemon properties at an exact position.
     */
    public static Optional<PokemonEntity> spawn(
            ServerLevel level,
            Vec3 position,
            String query
    ) {
        Objects.requireNonNull(level, "level");
        return spawn(level, position, properties(query));
    }

    /**
     * Spawns a Pokémon from native Cobblemon properties using a player as the
     * roll context.
     */
    public static Optional<PokemonEntity> spawn(
            ServerLevel level,
            Vec3 position,
            String query,
            ServerPlayer player
    ) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(player, "player");
        return spawn(level, position, properties(query), player);
    }

    /**
     * Spawns a Pokémon at the center of a block position.
     */
    public static Optional<PokemonEntity> spawn(
            ServerLevel level,
            BlockPos position,
            String query
    ) {
        Objects.requireNonNull(position, "position");
        return spawn(level, Vec3.atCenterOf(position), query);
    }

    /**
     * Spawns a Pokémon entity from already prepared properties.
     */
    public static Optional<PokemonEntity> spawn(
            ServerLevel level,
            Vec3 position,
            PokemonProperties properties
    ) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(properties, "properties");

        return add(level, position, properties.createEntity(level));
    }

    /**
     * Spawns a Pokémon entity from already prepared properties using a player
     * as the roll context.
     */
    public static Optional<PokemonEntity> spawn(
            ServerLevel level,
            Vec3 position,
            PokemonProperties properties,
            ServerPlayer player
    ) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(properties, "properties");
        Objects.requireNonNull(player, "player");

        return add(level, position, properties.createEntity(level, player));
    }

    /**
     * Convenience replacement for the common dynamic
     * {@code /pokespawn <species> level=<n> shiny=<bool>} pattern.
     *
     * <p>The species is registry-validated first. Level normalization is then
     * delegated to Cobblemon's property parser.</p>
     */
    public static Optional<PokemonEntity> spawnSpecies(
            ServerLevel level,
            Vec3 position,
            String speciesId,
            int pokemonLevel,
            boolean shiny
    ) {
        Species species = PokemonSpeciesResolver.require(speciesId);
        String query = "species=" + species.getResourceIdentifier()
                + " level=" + pokemonLevel
                + " shiny=" + shiny;
        return spawn(level, position, query);
    }

    private static Optional<PokemonEntity> add(
            ServerLevel level,
            Vec3 position,
            PokemonEntity entity
    ) {
        BlockPos blockPos = BlockPos.containing(position);
        if (!Level.isInSpawnableBounds(blockPos)) {
            return Optional.empty();
        }

        entity.moveTo(
                position.x,
                position.y,
                position.z,
                entity.getYRot(),
                entity.getXRot()
        );
        entity.getEntityData().set(
                PokemonEntity.SPAWN_DIRECTION,
                entity.getRandom().nextFloat() * 360.0F
        );

        if (!level.addFreshEntity(entity)) {
            return Optional.empty();
        }

        return Optional.of(entity);
    }
}
