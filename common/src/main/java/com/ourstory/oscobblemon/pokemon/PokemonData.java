package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
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
    private static final String CODEC_VALUE_KEY = "value";

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
     * Decodes a typed value from a data section, using the default only when
     * the section is absent. Malformed stored values remain an error.
     */
    public static <T> DataResult<T> readOrDefault(
            Pokemon pokemon,
            String key,
            Codec<T> codec,
            T defaultValue
    ) {
        return readOrDefault(pokemon, key, codec, defaultValue, NbtOps.INSTANCE);
    }

    /**
     * Decodes a typed value using the supplied operations. Use registry-aware
     * operations for codecs that require registry access.
     */
    public static <T> DataResult<T> readOrDefault(
            Pokemon pokemon,
            String key,
            Codec<T> codec,
            T defaultValue,
            DynamicOps<Tag> ops
    ) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(ops, "ops");

        CompoundTag persistentData = pokemon.getPersistentData();
        if (!persistentData.contains(key)) {
            return DataResult.success(defaultValue);
        }
        if (!persistentData.contains(key, Tag.TAG_COMPOUND)) {
            return DataResult.error(() -> "Persistent data section '" + key + "' is not a compound");
        }

        CompoundTag section = persistentData.getCompound(key);
        Tag value = section.get(CODEC_VALUE_KEY);
        if (value == null) {
            return DataResult.error(() -> "Persistent data section '" + key + "' has no typed value");
        }
        return codec.parse(ops, value);
    }

    /**
     * Encodes a typed value into a data section and notifies Cobblemon after a
     * successful encode. Encoding errors do not mutate Pokémon data.
     */
    public static <T> DataResult<T> write(Pokemon pokemon, String key, Codec<T> codec, T value) {
        return write(pokemon, key, codec, value, NbtOps.INSTANCE);
    }

    /**
     * Encodes a typed value using the supplied operations. Use registry-aware
     * operations for codecs that require registry access.
     */
    public static <T> DataResult<T> write(
            Pokemon pokemon,
            String key,
            Codec<T> codec,
            T value,
            DynamicOps<Tag> ops
    ) {
        Objects.requireNonNull(pokemon, "pokemon");
        validateKey(key);
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(ops, "ops");

        DataResult<Tag> encoded = codec.encodeStart(ops, value);
        if (encoded.error().isPresent()) {
            return encoded.map(ignored -> value);
        }

        CompoundTag section = new CompoundTag();
        section.put(CODEC_VALUE_KEY, encoded.result().orElseThrow().copy());
        pokemon.getPersistentData().put(key, section);
        pokemon.onChange(null);
        return encoded.map(ignored -> value);
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
