package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Safe access to namespaced persistent data stored on a Cobblemon Pokémon.
 *
 * <p>Reads return a copy. Updates replace the selected section and notify
 * Cobblemon, so the change is propagated to the Pokémon's storage and client
 * state when applicable.</p>
 */
public final class PokemonData {
    private PokemonData() {
    }

    /**
     * Reads a data section without exposing the live persistent-data tree.
     */
    public static Optional<CompoundTag> read(Pokemon pokemon, String key) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);

        CompoundTag persistentData = pokemon.getPersistentData();
        if (!persistentData.contains(key, Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }
        return Optional.of(persistentData.getCompound(key).copy());
    }

    /**
     * Updates a data section and marks the Pokémon as changed.
     *
     * <p>The updater receives a copy of the existing section. If the section
     * does not exist, it receives a new empty compound.</p>
     */
    public static void update(Pokemon pokemon, String key, Consumer<CompoundTag> updater) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);
        Objects.requireNonNull(updater, "updater");

        CompoundTag persistentData = pokemon.getPersistentData();
        CompoundTag section = persistentData.contains(key, Tag.TAG_COMPOUND)
                ? persistentData.getCompound(key).copy()
                : new CompoundTag();
        updater.accept(section);
        persistentData.put(key, section);
        pokemon.onChange(null);
    }

    /**
     * Returns whether a compound data section exists for the key.
     */
    public static boolean contains(Pokemon pokemon, String key) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);
        return pokemon.getPersistentData().contains(key, Tag.TAG_COMPOUND);
    }

    /**
     * Removes a data section and marks the Pokémon as changed when necessary.
     */
    public static void remove(Pokemon pokemon, String key) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);

        CompoundTag persistentData = pokemon.getPersistentData();
        if (persistentData.contains(key)) {
            persistentData.remove(key);
            pokemon.onChange(null);
        }
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
    }
}
