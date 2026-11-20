package com.ourstory.oscobblemon.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.reactive.SettableObservable;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

final class PokemonTestFixture {
    private static final Unsafe UNSAFE = unsafe();

    private PokemonTestFixture() {
    }

    // shortcut: initializes only fields used by PokemonData; replace when Cobblemon exposes a headless fixture.
    static Pokemon newPokemon() {
        try {
            Pokemon pokemon = (Pokemon) UNSAFE.allocateInstance(Pokemon.class);
            setObjectField(pokemon, "persistentData", new CompoundTag());
            setObjectField(pokemon, "storeCoordinates", new SettableObservable<>(null));
            return pokemon;
        } catch (InstantiationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    static <T> T withRegisteredSpecies(
            ResourceLocation id,
            Species species,
            Supplier<T> action
    ) {
        Map<ResourceLocation, Species> original = new LinkedHashMap<>();
        PokemonSpecies.INSTANCE.getSpecies().forEach(
                registered -> original.put(registered.getResourceIdentifier(), registered)
        );

        species.setResourceIdentifier(id);
        try {
            PokemonSpecies.INSTANCE.reload(Map.of(id, species));
            return action.get();
        } finally {
            PokemonSpecies.INSTANCE.reload(original);
        }
    }

    private static void setObjectField(Pokemon pokemon, String name, Object value) {
        try {
            Field field = Pokemon.class.getDeclaredField(name);
            UNSAFE.putObject(pokemon, UNSAFE.objectFieldOffset(field), value);
        } catch (NoSuchFieldException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static Unsafe unsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
