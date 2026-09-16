package com.ourstory.oscobblemon.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonNicknamedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSeenEvent;
import com.cobblemon.mod.common.api.events.pokemon.TradeEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.reactive.Observable;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Java-friendly subscriptions for Cobblemon events commonly reused by mods.
 *
 * <p>This class is intentionally small. It provides a stable compatibility
 * boundary around high-value Cobblemon event names without trying to mirror
 * the entire {@link CobblemonEvents} object.</p>
 */
public final class CobblemonEventHooks {
    private CobblemonEventHooks() {
    }

    public static ObservableSubscription<PokemonCapturedEvent> onCapture(
            Consumer<PokemonCapturedEvent> handler
    ) {
        return onCapture(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonCapturedEvent> onCapture(
            Priority priority,
            Consumer<PokemonCapturedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_CAPTURED, priority, handler);
    }

    public static ObservableSubscription<BattleVictoryEvent> onBattleVictory(
            Consumer<BattleVictoryEvent> handler
    ) {
        return onBattleVictory(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<BattleVictoryEvent> onBattleVictory(
            Priority priority,
            Consumer<BattleVictoryEvent> handler
    ) {
        return subscribe(CobblemonEvents.BATTLE_VICTORY, priority, handler);
    }

    public static ObservableSubscription<BattleFaintedEvent> onBattleFainted(
            Consumer<BattleFaintedEvent> handler
    ) {
        return onBattleFainted(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<BattleFaintedEvent> onBattleFainted(
            Priority priority,
            Consumer<BattleFaintedEvent> handler
    ) {
        return subscribe(CobblemonEvents.BATTLE_FAINTED, priority, handler);
    }

    public static ObservableSubscription<BattleFledEvent> onBattleFled(
            Consumer<BattleFledEvent> handler
    ) {
        return onBattleFled(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<BattleFledEvent> onBattleFled(
            Priority priority,
            Consumer<BattleFledEvent> handler
    ) {
        return subscribe(CobblemonEvents.BATTLE_FLED, priority, handler);
    }

    public static ObservableSubscription<PokemonSeenEvent> onPokemonSeen(
            Consumer<PokemonSeenEvent> handler
    ) {
        return onPokemonSeen(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonSeenEvent> onPokemonSeen(
            Priority priority,
            Consumer<PokemonSeenEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_SEEN, priority, handler);
    }

    public static ObservableSubscription<PokedexDataChangedEvent.Post> onPokedexChanged(
            Consumer<PokedexDataChangedEvent.Post> handler
    ) {
        return onPokedexChanged(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokedexDataChangedEvent.Post> onPokedexChanged(
            Priority priority,
            Consumer<PokedexDataChangedEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.POKEDEX_DATA_CHANGED_POST, priority, handler);
    }

    public static ObservableSubscription<HatchEggEvent.Post> onHatch(
            Consumer<HatchEggEvent.Post> handler
    ) {
        return onHatch(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<HatchEggEvent.Post> onHatch(
            Priority priority,
            Consumer<HatchEggEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.HATCH_EGG_POST, priority, handler);
    }

    public static ObservableSubscription<TradeEvent.Post> onTrade(
            Consumer<TradeEvent.Post> handler
    ) {
        return onTrade(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<TradeEvent.Post> onTrade(
            Priority priority,
            Consumer<TradeEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.TRADE_EVENT_POST, priority, handler);
    }

    public static ObservableSubscription<ReleasePokemonEvent.Post> onRelease(
            Consumer<ReleasePokemonEvent.Post> handler
    ) {
        return onRelease(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<ReleasePokemonEvent.Post> onRelease(
            Priority priority,
            Consumer<ReleasePokemonEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_RELEASED_EVENT_POST, priority, handler);
    }

    public static ObservableSubscription<PokemonNicknamedEvent> onNickname(
            Consumer<PokemonNicknamedEvent> handler
    ) {
        return onNickname(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonNicknamedEvent> onNickname(
            Priority priority,
            Consumer<PokemonNicknamedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_NICKNAMED, priority, handler);
    }

    public static ObservableSubscription<PokemonGainedEvent> onPokemonGained(
            Consumer<PokemonGainedEvent> handler
    ) {
        return onPokemonGained(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonGainedEvent> onPokemonGained(
            Priority priority,
            Consumer<PokemonGainedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_GAINED, priority, handler);
    }

    public static ObservableSubscription<LevelUpEvent> onLevelUp(
            Consumer<LevelUpEvent> handler
    ) {
        return onLevelUp(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<LevelUpEvent> onLevelUp(
            Priority priority,
            Consumer<LevelUpEvent> handler
    ) {
        return subscribe(CobblemonEvents.LEVEL_UP_EVENT, priority, handler);
    }

    public static ObservableSubscription<ExperienceGainedEvent.Pre> onExperienceGainedPre(
            Consumer<ExperienceGainedEvent.Pre> handler
    ) {
        return onExperienceGainedPre(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<ExperienceGainedEvent.Pre> onExperienceGainedPre(
            Priority priority,
            Consumer<ExperienceGainedEvent.Pre> handler
    ) {
        return subscribe(CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE, priority, handler);
    }

    public static ObservableSubscription<ExperienceGainedEvent.Post> onExperienceGainedPost(
            Consumer<ExperienceGainedEvent.Post> handler
    ) {
        return onExperienceGainedPost(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<ExperienceGainedEvent.Post> onExperienceGainedPost(
            Priority priority,
            Consumer<ExperienceGainedEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.EXPERIENCE_GAINED_EVENT_POST, priority, handler);
    }

    private static <T> ObservableSubscription<T> subscribe(
            Observable<T> observable,
            Priority priority,
            Consumer<T> handler
    ) {
        Objects.requireNonNull(observable, "observable");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(handler, "handler");
        return observable.subscribe(priority, handler);
    }
}
