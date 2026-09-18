package com.ourstory.oscobblemon.event;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleFledEvent;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.FormeChangeEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.MegaEvolutionEvent;
import com.cobblemon.mod.common.api.events.battles.instruction.TerastallizationEvent;
import com.cobblemon.mod.common.api.events.entity.PokemonEntityLoadEvent;
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveEvent;
import com.cobblemon.mod.common.api.events.entity.PokemonEntitySaveToWorldEvent;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.events.pokemon.ExperienceGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent;
import com.cobblemon.mod.common.api.events.pokemon.LevelUpEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonAspectsChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokedexDataChangedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonGainedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonNicknamedEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonRecallEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSentEvent;
import com.cobblemon.mod.common.api.events.pokemon.PokemonSeenEvent;
import com.cobblemon.mod.common.api.events.pokemon.TradeEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionAcceptedEvent;
import com.cobblemon.mod.common.api.events.pokemon.evolution.EvolutionCompleteEvent;
import com.cobblemon.mod.common.api.events.pokemon.healing.PokemonHealedEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokeBallCaptureCalculatedEvent;
import com.cobblemon.mod.common.api.events.pokeball.PokemonCatchRateEvent;
import com.cobblemon.mod.common.api.events.storage.ReleasePokemonEvent;
import com.cobblemon.mod.common.api.reactive.Observable;
import com.cobblemon.mod.common.api.reactive.ObservableSubscription;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;

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

    public static ObservableSubscription<PokemonCatchRateEvent> onCaptureRate(
            Consumer<PokemonCatchRateEvent> handler
    ) {
        return onCaptureRate(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonCatchRateEvent> onCaptureRate(
            Priority priority,
            Consumer<PokemonCatchRateEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_CATCH_RATE, priority, handler);
    }

    public static ObservableSubscription<PokeBallCaptureCalculatedEvent> onCaptureCalculated(
            Consumer<PokeBallCaptureCalculatedEvent> handler
    ) {
        return onCaptureCalculated(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokeBallCaptureCalculatedEvent> onCaptureCalculated(
            Priority priority,
            Consumer<PokeBallCaptureCalculatedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKE_BALL_CAPTURE_CALCULATED, priority, handler);
    }

    public static ObservableSubscription<EvolutionAcceptedEvent> onEvolutionAccepted(
            Consumer<EvolutionAcceptedEvent> handler
    ) {
        return onEvolutionAccepted(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<EvolutionAcceptedEvent> onEvolutionAccepted(
            Priority priority,
            Consumer<EvolutionAcceptedEvent> handler
    ) {
        return subscribe(CobblemonEvents.EVOLUTION_ACCEPTED, priority, handler);
    }

    public static ObservableSubscription<EvolutionCompleteEvent> onEvolutionComplete(
            Consumer<EvolutionCompleteEvent> handler
    ) {
        return onEvolutionComplete(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<EvolutionCompleteEvent> onEvolutionComplete(
            Priority priority,
            Consumer<EvolutionCompleteEvent> handler
    ) {
        return subscribe(CobblemonEvents.EVOLUTION_COMPLETE, priority, handler);
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

    public static ObservableSubscription<BattleStartedEvent.Pre> onBattleStartedPre(
            Consumer<BattleStartedEvent.Pre> handler
    ) {
        return onBattleStartedPre(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<BattleStartedEvent.Pre> onBattleStartedPre(
            Priority priority,
            Consumer<BattleStartedEvent.Pre> handler
    ) {
        return subscribe(CobblemonEvents.BATTLE_STARTED_PRE, priority, handler);
    }

    public static ObservableSubscription<BattleStartedEvent.Post> onBattleStartedPost(
            Consumer<BattleStartedEvent.Post> handler
    ) {
        return onBattleStartedPost(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<BattleStartedEvent.Post> onBattleStartedPost(
            Priority priority,
            Consumer<BattleStartedEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.BATTLE_STARTED_POST, priority, handler);
    }

    public static ObservableSubscription<MegaEvolutionEvent> onMegaEvolution(
            Consumer<MegaEvolutionEvent> handler
    ) {
        return onMegaEvolution(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<MegaEvolutionEvent> onMegaEvolution(
            Priority priority,
            Consumer<MegaEvolutionEvent> handler
    ) {
        return subscribe(CobblemonEvents.MEGA_EVOLUTION, priority, handler);
    }

    public static ObservableSubscription<TerastallizationEvent> onTerastallization(
            Consumer<TerastallizationEvent> handler
    ) {
        return onTerastallization(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<TerastallizationEvent> onTerastallization(
            Priority priority,
            Consumer<TerastallizationEvent> handler
    ) {
        return subscribe(CobblemonEvents.TERASTALLIZATION, priority, handler);
    }

    public static ObservableSubscription<FormeChangeEvent> onFormeChange(
            Consumer<FormeChangeEvent> handler
    ) {
        return onFormeChange(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<FormeChangeEvent> onFormeChange(
            Priority priority,
            Consumer<FormeChangeEvent> handler
    ) {
        return subscribe(CobblemonEvents.FORME_CHANGE, priority, handler);
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

    public static ObservableSubscription<PokemonAspectsChangedEvent> onPokemonAspectsChanged(
            Consumer<PokemonAspectsChangedEvent> handler
    ) {
        return onPokemonAspectsChanged(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonAspectsChangedEvent> onPokemonAspectsChanged(
            Priority priority,
            Consumer<PokemonAspectsChangedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_ASPECTS_CHANGED, priority, handler);
    }

    public static ObservableSubscription<PokedexDataChangedEvent.Pre> onPokedexChangedPre(
            Consumer<PokedexDataChangedEvent.Pre> handler
    ) {
        return onPokedexChangedPre(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokedexDataChangedEvent.Pre> onPokedexChangedPre(
            Priority priority,
            Consumer<PokedexDataChangedEvent.Pre> handler
    ) {
        return subscribe(CobblemonEvents.POKEDEX_DATA_CHANGED_PRE, priority, handler);
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

    public static ObservableSubscription<PokemonFaintedEvent> onPokemonFainted(
            Consumer<PokemonFaintedEvent> handler
    ) {
        return onPokemonFainted(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonFaintedEvent> onPokemonFainted(
            Priority priority,
            Consumer<PokemonFaintedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_FAINTED, priority, handler);
    }

    public static ObservableSubscription<PokemonHealedEvent> onPokemonHealed(
            Consumer<PokemonHealedEvent> handler
    ) {
        return onPokemonHealed(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonHealedEvent> onPokemonHealed(
            Priority priority,
            Consumer<PokemonHealedEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_HEALED, priority, handler);
    }

    public static ObservableSubscription<PokemonSentEvent.Pre> onPokemonSentPre(
            Consumer<PokemonSentEvent.Pre> handler
    ) {
        return onPokemonSentPre(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonSentEvent.Pre> onPokemonSentPre(
            Priority priority,
            Consumer<PokemonSentEvent.Pre> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_SENT_PRE, priority, handler);
    }

    public static ObservableSubscription<PokemonSentEvent.Post> onPokemonSentPost(
            Consumer<PokemonSentEvent.Post> handler
    ) {
        return onPokemonSentPost(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonSentEvent.Post> onPokemonSentPost(
            Priority priority,
            Consumer<PokemonSentEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_SENT_POST, priority, handler);
    }

    public static ObservableSubscription<PokemonRecallEvent.Pre> onPokemonRecallPre(
            Consumer<PokemonRecallEvent.Pre> handler
    ) {
        return onPokemonRecallPre(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonRecallEvent.Pre> onPokemonRecallPre(
            Priority priority,
            Consumer<PokemonRecallEvent.Pre> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_RECALL_PRE, priority, handler);
    }

    public static ObservableSubscription<PokemonRecallEvent.Post> onPokemonRecallPost(
            Consumer<PokemonRecallEvent.Post> handler
    ) {
        return onPokemonRecallPost(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonRecallEvent.Post> onPokemonRecallPost(
            Priority priority,
            Consumer<PokemonRecallEvent.Post> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_RECALL_POST, priority, handler);
    }

    public static ObservableSubscription<PokemonEntitySaveEvent> onPokemonEntitySave(
            Consumer<PokemonEntitySaveEvent> handler
    ) {
        return onPokemonEntitySave(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonEntitySaveEvent> onPokemonEntitySave(
            Priority priority,
            Consumer<PokemonEntitySaveEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_ENTITY_SAVE, priority, handler);
    }

    public static ObservableSubscription<PokemonEntityLoadEvent> onPokemonEntityLoad(
            Consumer<PokemonEntityLoadEvent> handler
    ) {
        return onPokemonEntityLoad(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonEntityLoadEvent> onPokemonEntityLoad(
            Priority priority,
            Consumer<PokemonEntityLoadEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_ENTITY_LOAD, priority, handler);
    }

    public static ObservableSubscription<PokemonEntitySaveToWorldEvent> onPokemonEntitySaveToWorld(
            Consumer<PokemonEntitySaveToWorldEvent> handler
    ) {
        return onPokemonEntitySaveToWorld(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<PokemonEntitySaveToWorldEvent> onPokemonEntitySaveToWorld(
            Priority priority,
            Consumer<PokemonEntitySaveToWorldEvent> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_ENTITY_SAVE_TO_WORLD, priority, handler);
    }

    public static ObservableSubscription<SpawnEvent<PokemonEntity>> onPokemonEntitySpawn(
            Consumer<SpawnEvent<PokemonEntity>> handler
    ) {
        return onPokemonEntitySpawn(Priority.NORMAL, handler);
    }

    public static ObservableSubscription<SpawnEvent<PokemonEntity>> onPokemonEntitySpawn(
            Priority priority,
            Consumer<SpawnEvent<PokemonEntity>> handler
    ) {
        return subscribe(CobblemonEvents.POKEMON_ENTITY_SPAWN, priority, handler);
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
