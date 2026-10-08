package com.ourstory.oscobblemon.storage;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.storage.ClientParty;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.ourstory.oscobblemon.pokemon.PokemonSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only access to the Pokémon party synchronized to the current client.
 * Use these methods only from client-side code.
 */
public final class ClientPokemonStorage {
    private ClientPokemonStorage() {
    }

    /** Returns the current client's local party store. */
    public static ClientParty party() {
        return CobblemonClient.INSTANCE.getStorage().getParty();
    }

    /** Finds a Pokémon by UUID in the current client's party. */
    public static Optional<Pokemon> findInParty(UUID pokemonId) {
        return findInParty(party(), pokemonId);
    }

    /** Finds a Pokémon by UUID in the supplied client party. */
    public static Optional<Pokemon> findInParty(ClientParty party, UUID pokemonId) {
        Objects.requireNonNull(party, "party");
        Objects.requireNonNull(pokemonId, "pokemonId");
        return Optional.ofNullable(party.findByUUID(pokemonId));
    }

    /** Returns an immutable list of occupied slots in the current client's party. */
    public static List<Pokemon> partySnapshot() {
        return partySnapshot(party());
    }

    /** Returns an immutable list of occupied slots in the supplied client party. */
    public static List<Pokemon> partySnapshot(ClientParty party) {
        Objects.requireNonNull(party, "party");
        List<Pokemon> result = new ArrayList<>();
        for (int slot = 0; slot < party.getSlots().size(); slot++) {
            Pokemon pokemon = party.get(slot);
            if (pokemon != null) {
                result.add(pokemon);
            }
        }
        return List.copyOf(result);
    }

    /** Returns copied Pokémon values for every party slot, preserving empty slots and indexes. */
    public static List<Optional<PokemonSnapshot>> partySlotsSnapshot() {
        return partySlotsSnapshot(party());
    }

    /** Returns copied Pokémon values for every supplied party slot, preserving empty slots and indexes. */
    public static List<Optional<PokemonSnapshot>> partySlotsSnapshot(ClientParty party) {
        Objects.requireNonNull(party, "party");
        List<Optional<PokemonSnapshot>> result = new ArrayList<>(party.getSlots().size());
        for (int slot = 0; slot < party.getSlots().size(); slot++) {
            result.add(Optional.ofNullable(party.get(slot)).map(PokemonSnapshot::from));
        }
        return List.copyOf(result);
    }
}
