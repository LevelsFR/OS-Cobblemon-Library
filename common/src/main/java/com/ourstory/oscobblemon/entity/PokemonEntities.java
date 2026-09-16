package com.ourstory.oscobblemon.entity;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ourstory.oscobblemon.pokemon.PokemonIdentity;
import com.ourstory.oscobblemon.pokemon.PokemonIdentitySnapshot;

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
