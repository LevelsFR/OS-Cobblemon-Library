package com.ourstory.oscobblemon.battle;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Reusable inspection helpers for Cobblemon battles.
 *
 * <p>The public API returns stable Cobblemon concepts such as {@link Pokemon},
 * {@link BattleActor}, UUIDs and {@link BattleKind}. Internal battle Pokémon
 * wrappers are kept behind this class so consuming mods do not need to depend
 * on them directly.</p>
 */
public final class BattleInspector {
    private BattleInspector() {
    }

    /**
     * Classifies a battle using Cobblemon's built-in PvW, PvN and PvP flags.
     */
    public static BattleKind kind(PokemonBattle battle) {
        Objects.requireNonNull(battle, "battle");

        if (battle.isPvP()) {
            return BattleKind.PVP;
        }
        if (battle.isPvN()) {
            return BattleKind.TRAINER;
        }
        if (battle.isPvW()) {
            return BattleKind.WILD;
        }
        return BattleKind.OTHER;
    }

    public static boolean isWild(PokemonBattle battle) {
        return kind(battle) == BattleKind.WILD;
    }

    public static boolean isTrainer(PokemonBattle battle) {
        return kind(battle) == BattleKind.TRAINER;
    }

    public static boolean isPvP(PokemonBattle battle) {
        return kind(battle) == BattleKind.PVP;
    }

    /**
     * Returns an immutable snapshot of all actors in battle order.
     */
    public static List<BattleActor> actors(PokemonBattle battle) {
        Objects.requireNonNull(battle, "battle");

        List<BattleActor> result = new ArrayList<>();
        for (BattleActor actor : battle.getActors()) {
            result.add(actor);
        }
        return List.copyOf(result);
    }

    /**
     * Returns actors of the requested type.
     */
    public static List<BattleActor> actors(PokemonBattle battle, ActorType type) {
        Objects.requireNonNull(battle, "battle");
        Objects.requireNonNull(type, "type");

        List<BattleActor> result = new ArrayList<>();
        for (BattleActor actor : battle.getActors()) {
            if (actor.getType() == type) {
                result.add(actor);
            }
        }
        return List.copyOf(result);
    }

    public static List<BattleActor> playerActors(PokemonBattle battle) {
        return actors(battle, ActorType.PLAYER);
    }

    public static List<BattleActor> npcActors(PokemonBattle battle) {
        return actors(battle, ActorType.NPC);
    }

    public static List<BattleActor> wildActors(PokemonBattle battle) {
        return actors(battle, ActorType.WILD);
    }

    /**
     * Returns all player UUIDs participating in the battle without duplicates.
     */
    public static Set<UUID> playerIds(PokemonBattle battle) {
        Objects.requireNonNull(battle, "battle");

        return playerIds(battle.getActors());
    }

    /**
     * Returns whether the given player UUID participates in the battle.
     */
    public static boolean containsPlayer(PokemonBattle battle, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerIds(battle).contains(playerId);
    }

    /**
     * Returns the normal Pokémon instance that fainted in a Cobblemon battle
     * event, hiding the internal BattlePokemon wrapper.
     */
    public static Pokemon faintedPokemon(BattleFaintedEvent event) {
        Objects.requireNonNull(event, "event");
        return event.getKilled().getEffectedPokemon();
    }

    /**
     * Returns an immutable snapshot of victory-event winners.
     */
    public static List<BattleActor> winners(BattleVictoryEvent event) {
        Objects.requireNonNull(event, "event");
        return List.copyOf(event.getWinners());
    }

    /**
     * Returns an immutable snapshot of victory-event losers.
     */
    public static List<BattleActor> losers(BattleVictoryEvent event) {
        Objects.requireNonNull(event, "event");
        return List.copyOf(event.getLosers());
    }

    /**
     * Returns player UUIDs among the winning actors.
     */
    public static Set<UUID> winningPlayerIds(BattleVictoryEvent event) {
        Objects.requireNonNull(event, "event");
        return playerIds(event.getWinners());
    }

    /**
     * Returns player UUIDs among the losing actors.
     */
    public static Set<UUID> losingPlayerIds(BattleVictoryEvent event) {
        Objects.requireNonNull(event, "event");
        return playerIds(event.getLosers());
    }

    /**
     * Returns whether a player is among the winning actors.
     */
    public static boolean didPlayerWin(BattleVictoryEvent event, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return winningPlayerIds(event).contains(playerId);
    }

    /**
     * Returns whether a player is among the losing actors.
     */
    public static boolean didPlayerLose(BattleVictoryEvent event, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return losingPlayerIds(event).contains(playerId);
    }

    /**
     * Returns all normal Pokémon represented by a collection of battle actors.
     */
    public static List<Pokemon> pokemon(Iterable<BattleActor> actors) {
        Objects.requireNonNull(actors, "actors");

        List<Pokemon> result = new ArrayList<>();
        for (BattleActor actor : actors) {
            for (BattlePokemon battlePokemon : actor.getPokemonList()) {
                result.add(battlePokemon.getEffectedPokemon());
            }
        }
        return List.copyOf(result);
    }

    /**
     * Returns an immutable snapshot of all underlying Pokémon in the battle.
     *
     * <p>This hides Cobblemon's internal {@code BattlePokemon} wrapper from
     * consumers. The returned {@link Pokemon} instances remain live objects.</p>
     */
    public static List<Pokemon> pokemon(PokemonBattle battle) {
        Objects.requireNonNull(battle, "battle");

        List<Pokemon> result = new ArrayList<>();
        for (BattleActor actor : battle.getActors()) {
            for (BattlePokemon battlePokemon : actor.getPokemonList()) {
                result.add(battlePokemon.getEffectedPokemon());
            }
        }
        return List.copyOf(result);
    }

    /**
     * Returns all underlying Pokémon owned by actors of the requested type.
     */
    public static List<Pokemon> pokemon(PokemonBattle battle, ActorType type) {
        Objects.requireNonNull(type, "type");
        return pokemon(actors(battle, type));
    }

    private static Set<UUID> playerIds(Iterable<BattleActor> actors) {
        LinkedHashSet<UUID> result = new LinkedHashSet<>();
        for (BattleActor actor : actors) {
            for (UUID playerId : actor.getPlayerUUIDs()) {
                result.add(playerId);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
