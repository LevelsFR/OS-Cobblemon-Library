package com.ourstory.oscobblemon.storage;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Shared access and lookup helpers for player-owned Cobblemon storage.
 *
 * <p>This class centralizes the common Party/PC access patterns used by mods so
 * Cobblemon storage API changes can be handled in one place.</p>
 *
 * <p>The methods intentionally follow Cobblemon's normal storage threading
 * expectations. They do not add asynchronous access or caching.</p>
 */
public final class PokemonStorage {
    private PokemonStorage() {
    }

    /**
     * Returns the player's active party store.
     */
    public static PlayerPartyStore party(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        return Cobblemon.INSTANCE.getStorage().getParty(player);
    }

    /**
     * Returns a player's party store by UUID.
     *
     * <p>This overload is useful when only server registry access and a player
     * UUID are available.</p>
     */
    public static PlayerPartyStore party(UUID playerId, RegistryAccess registryAccess) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(registryAccess, "registryAccess");
        return Cobblemon.INSTANCE.getStorage().getParty(playerId, registryAccess);
    }

    /**
     * Returns the player's PC store.
     */
    public static PCStore pc(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        return Cobblemon.INSTANCE.getStorage().getPC(player);
    }

    /**
     * Returns a player's PC store by UUID.
     */
    public static PCStore pc(UUID playerId, RegistryAccess registryAccess) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(registryAccess, "registryAccess");
        return Cobblemon.INSTANCE.getStorage().getPC(playerId, registryAccess);
    }

    /**
     * Finds a Pokémon by UUID in the player's party.
     */
    public static Optional<Pokemon> findInParty(ServerPlayer player, UUID pokemonId) {
        Objects.requireNonNull(player, "player");
        return findInParty(party(player), pokemonId);
    }

    /**
     * Finds a Pokémon by UUID in the given party store.
     */
    public static Optional<Pokemon> findInParty(PlayerPartyStore party, UUID pokemonId) {
        Objects.requireNonNull(party, "party");
        Objects.requireNonNull(pokemonId, "pokemonId");
        return Optional.ofNullable(party.get(pokemonId));
    }

    /**
     * Finds a Pokémon by UUID in the player's PC.
     */
    public static Optional<Pokemon> findInPC(ServerPlayer player, UUID pokemonId) {
        Objects.requireNonNull(player, "player");
        return findInPC(pc(player), pokemonId);
    }

    /**
     * Finds a Pokémon by UUID in the given PC store.
     */
    public static Optional<Pokemon> findInPC(PCStore pc, UUID pokemonId) {
        Objects.requireNonNull(pc, "pc");
        Objects.requireNonNull(pokemonId, "pokemonId");
        return Optional.ofNullable(pc.get(pokemonId));
    }

    /**
     * Finds a player-owned Pokémon, checking the party before the PC.
     */
    public static Optional<Pokemon> findOwned(ServerPlayer player, UUID pokemonId) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pokemonId, "pokemonId");

        PlayerPartyStore party = party(player);
        Pokemon partyPokemon = party.get(pokemonId);
        if (partyPokemon != null) {
            return Optional.of(partyPokemon);
        }

        return Optional.ofNullable(pc(player).get(pokemonId));
    }

    /**
     * Finds a player-owned Pokémon by owner UUID, checking the party before the PC.
     */
    public static Optional<Pokemon> findOwned(
            UUID playerId,
            RegistryAccess registryAccess,
            UUID pokemonId
    ) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(registryAccess, "registryAccess");
        Objects.requireNonNull(pokemonId, "pokemonId");

        PlayerPartyStore party = party(playerId, registryAccess);
        Pokemon partyPokemon = party.get(pokemonId);
        if (partyPokemon != null) {
            return Optional.of(partyPokemon);
        }

        return Optional.ofNullable(pc(playerId, registryAccess).get(pokemonId));
    }

    /**
     * Returns the zero-based party slot containing the given Pokémon UUID.
     */
    public static OptionalInt partySlot(ServerPlayer player, UUID pokemonId) {
        Objects.requireNonNull(player, "player");
        return partySlot(party(player), pokemonId);
    }

    /**
     * Returns the zero-based party slot containing the given Pokémon UUID.
     */
    public static OptionalInt partySlot(PlayerPartyStore party, UUID pokemonId) {
        Objects.requireNonNull(party, "party");
        Objects.requireNonNull(pokemonId, "pokemonId");

        for (int slot = 0; slot < party.size(); slot++) {
            Pokemon pokemon = party.get(slot);
            if (pokemon != null && pokemonId.equals(pokemon.getUuid())) {
                return OptionalInt.of(slot);
            }
        }

        return OptionalInt.empty();
    }

    /**
     * Returns an immutable snapshot of the occupied party slots in party order.
     */
    public static List<Pokemon> partySnapshot(ServerPlayer player) {
        Objects.requireNonNull(player, "player");
        return partySnapshot(party(player));
    }

    /**
     * Returns an immutable snapshot of the occupied party slots in party order.
     */
    public static List<Pokemon> partySnapshot(PlayerPartyStore party) {
        Objects.requireNonNull(party, "party");

        List<Pokemon> result = new ArrayList<>(party.occupied());
        for (int slot = 0; slot < party.size(); slot++) {
            Pokemon pokemon = party.get(slot);
            if (pokemon != null) {
                result.add(pokemon);
            }
        }

        return List.copyOf(result);
    }

    /**
     * Returns an immutable snapshot containing all player-owned Pokémon,
     * with party Pokémon first followed by PC Pokémon.
     */
    public static List<Pokemon> ownedSnapshot(ServerPlayer player) {
        Objects.requireNonNull(player, "player");

        List<Pokemon> result = new ArrayList<>();
        PlayerPartyStore party = party(player);
        result.addAll(partySnapshot(party));

        for (Pokemon pokemon : pc(player)) {
            result.add(pokemon);
        }

        return List.copyOf(result);
    }

    /**
     * Finds the first party Pokémon matching the supplied predicate.
     */
    public static Optional<Pokemon> findFirstInParty(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        return findFirstInParty(party(player), predicate);
    }

    /**
     * Finds the first Pokémon in a party store matching the supplied predicate.
     */
    public static Optional<Pokemon> findFirstInParty(
            PlayerPartyStore party,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(party, "party");
        Objects.requireNonNull(predicate, "predicate");

        for (int slot = 0; slot < party.size(); slot++) {
            Pokemon pokemon = party.get(slot);
            if (pokemon != null && predicate.test(pokemon)) {
                return Optional.of(pokemon);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns all party Pokémon matching the supplied predicate in party order.
     */
    public static List<Pokemon> findAllInParty(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");

        List<Pokemon> result = new ArrayList<>();
        for (Pokemon pokemon : partySnapshot(player)) {
            if (predicate.test(pokemon)) {
                result.add(pokemon);
            }
        }
        return List.copyOf(result);
    }

    /**
     * Finds the first PC Pokémon matching the supplied predicate.
     */
    public static Optional<Pokemon> findFirstInPC(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        return findFirstInPC(pc(player), predicate);
    }

    /**
     * Finds the first Pokémon in a PC store matching the supplied predicate.
     */
    public static Optional<Pokemon> findFirstInPC(
            PCStore pc,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(pc, "pc");
        Objects.requireNonNull(predicate, "predicate");

        for (Pokemon pokemon : pc) {
            if (predicate.test(pokemon)) {
                return Optional.of(pokemon);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns all PC Pokémon matching the supplied predicate in store order.
     */
    public static List<Pokemon> findAllInPC(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        return findAllInPC(pc(player), predicate);
    }

    /**
     * Returns all Pokémon in a PC store matching the supplied predicate.
     */
    public static List<Pokemon> findAllInPC(
            PCStore pc,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(pc, "pc");
        Objects.requireNonNull(predicate, "predicate");

        List<Pokemon> result = new ArrayList<>();
        for (Pokemon pokemon : pc) {
            if (predicate.test(pokemon)) {
                result.add(pokemon);
            }
        }
        return List.copyOf(result);
    }

    /**
     * Finds the first owned Pokémon matching the supplied predicate, checking
     * the party before the PC.
     */
    public static Optional<Pokemon> findFirstOwned(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");

        Optional<Pokemon> partyMatch = findFirstInParty(player, predicate);
        return partyMatch.isPresent() ? partyMatch : findFirstInPC(player, predicate);
    }

    /**
     * Returns all owned Pokémon matching the supplied predicate, with party
     * matches first followed by PC matches.
     */
    public static List<Pokemon> findAllOwned(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");

        List<Pokemon> result = new ArrayList<>();
        for (Pokemon pokemon : ownedSnapshot(player)) {
            if (predicate.test(pokemon)) {
                result.add(pokemon);
            }
        }
        return List.copyOf(result);
    }

    /**
     * Counts party Pokémon matching the supplied predicate.
     */
    public static int countInParty(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");

        int count = 0;
        PlayerPartyStore party = party(player);
        for (int slot = 0; slot < party.size(); slot++) {
            Pokemon pokemon = party.get(slot);
            if (pokemon != null && predicate.test(pokemon)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts PC Pokémon matching the supplied predicate.
     */
    public static int countInPC(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");

        int count = 0;
        for (Pokemon pokemon : pc(player)) {
            if (predicate.test(pokemon)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts all owned Pokémon matching the supplied predicate.
     */
    public static int countOwned(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(predicate, "predicate");
        return countInParty(player, predicate) + countInPC(player, predicate);
    }

    /**
     * Returns whether at least one owned Pokémon matches the supplied predicate.
     */
    public static boolean anyOwned(
            ServerPlayer player,
            Predicate<Pokemon> predicate
    ) {
        return findFirstOwned(player, predicate).isPresent();
    }

    /**
     * Returns whether the supplied Pokémon UUID is currently in the player's party.
     */
    public static boolean isInParty(ServerPlayer player, UUID pokemonId) {
        return findInParty(player, pokemonId).isPresent();
    }

    /**
     * Returns whether the supplied Pokémon UUID is currently in the player's PC.
     */
    public static boolean isInPC(ServerPlayer player, UUID pokemonId) {
        return findInPC(player, pokemonId).isPresent();
    }
}
