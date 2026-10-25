package com.ourstory.oscobblemon.entity;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ourstory.oscobblemon.pokemon.PokemonIdentity;
import com.ourstory.oscobblemon.pokemon.PokemonIdentitySnapshot;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Shared bridge helpers for working with Cobblemon {@link PokemonEntity}
 * instances without repeating entity-to-Pokemon access across mods.
 *
 * <p>This class intentionally stays read-only. Movement, AI, despawning,
 * persistence and other gameplay behavior remain the responsibility of the
 * consuming mod.</p>
 */
public final class PokemonEntities {
    private static final Reference2DoubleOpenHashMap<PokemonEntity> NEAREST_PLAYER_DISTANCE_SQUARED =
            new Reference2DoubleOpenHashMap<>(256);
    private static MinecraftServer distanceCacheServer;
    private static int distanceCacheTick = Integer.MIN_VALUE;

    static {
        NEAREST_PLAYER_DISTANCE_SQUARED.defaultReturnValue(Double.NaN);
    }

    private PokemonEntities() {
    }

    /**
     * Returns the live {@link Pokemon} represented by the entity.
     */
    public static Pokemon pokemon(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return Objects.requireNonNull(entity.getPokemon(), "entity.pokemon");
    }

    /**
     * Returns the stable UUID of the Pokémon represented by the entity.
     */
    public static UUID pokemonId(PokemonEntity entity) {
        return pokemon(entity).getUuid();
    }

    /**
     * Creates an immutable identity snapshot for the Pokémon represented by the
     * entity.
     */
    public static PokemonIdentitySnapshot identity(PokemonEntity entity) {
        return PokemonIdentity.snapshot(pokemon(entity));
    }

    /**
     * Returns the owner UUID when the entity currently has an owner.
     */
    public static Optional<UUID> ownerId(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return Optional.ofNullable(entity.getOwnerUUID());
    }

    /**
     * Returns whether the entity currently has an owner UUID.
     */
    public static boolean hasOwner(PokemonEntity entity) {
        return ownerId(entity).isPresent();
    }

    /**
     * Returns Cobblemon's current battle state for the entity.
     */
    public static boolean isBattling(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return entity.isBattling();
    }

    /**
     * Returns Cobblemon's current busy state for the entity.
     */
    public static boolean isBusy(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return entity.isBusy();
    }

    /**
     * Returns Cobblemon's current evolution state for the entity.
     */
    public static boolean isEvolving(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return entity.isEvolving();
    }

    /**
     * Returns whether the entity is alive and still present in the level.
     */
    public static boolean isAliveAndPresent(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return entity.isAlive() && !entity.isRemoved();
    }

    /**
     * Returns the nearest player's squared distance to this entity, cached for
     * the current server tick.
     *
     * <p>This query must be called on the server thread. It returns
     * {@link Double#MAX_VALUE} when the entity is not in a server level or no
     * players are present in its level.</p>
     */
    public static double nearestPlayerDistanceSquared(PokemonEntity entity) {
        Objects.requireNonNull(entity, "entity");
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return Double.MAX_VALUE;
        }

        MinecraftServer server = serverLevel.getServer();
        int currentTick = server.getTickCount();
        if (distanceCacheServer != server || distanceCacheTick != currentTick) {
            NEAREST_PLAYER_DISTANCE_SQUARED.clear();
            distanceCacheServer = server;
            distanceCacheTick = currentTick;
        }

        double cachedDistance = NEAREST_PLAYER_DISTANCE_SQUARED.getDouble(entity);
        if (!Double.isNaN(cachedDistance)) {
            return cachedDistance;
        }

        double nearestDistanceSquared = Double.MAX_VALUE;
        for (ServerPlayer player : serverLevel.players()) {
            double distanceSquared = player.distanceToSqr(entity);
            if (!Double.isNaN(distanceSquared) && distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared;
            }
        }

        NEAREST_PLAYER_DISTANCE_SQUARED.put(entity, nearestDistanceSquared);
        return nearestDistanceSquared;
    }

    /**
     * Clears the per-tick distance cache, for example when a server stops.
     */
    public static void clearNearestPlayerDistanceCache() {
        NEAREST_PLAYER_DISTANCE_SQUARED.clear();
        distanceCacheServer = null;
        distanceCacheTick = Integer.MIN_VALUE;
    }

    /**
     * Applies a Pokémon predicate directly to a Pokémon entity.
     *
     * <p>This works with ordinary Java predicates and with
     * {@code PokemonMatcher}, which implements {@code Predicate<Pokemon>}.</p>
     */
    public static boolean matches(
            PokemonEntity entity,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(predicate, "predicate");
        return predicate.test(pokemon(entity));
    }
}
