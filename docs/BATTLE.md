# Battle helpers

The `battle` package centralizes common read-only battle inspection.

## Classification

```java
BattleKind kind = BattleInspector.kind(battle);

if (BattleInspector.isTrainer(battle)) {
    // Player versus NPC trainer battle.
}
```

The classification uses Cobblemon's own battle flags:

- `WILD`: player versus wild Pokémon
- `TRAINER`: player versus NPC
- `PVP`: player actors on both sides
- `OTHER`: mixed or custom battle layouts

## Actors

```java
List<BattleActor> players = BattleInspector.playerActors(battle);
List<BattleActor> npcs = BattleInspector.npcActors(battle);
List<BattleActor> wild = BattleInspector.wildActors(battle);
```

You can also filter with any `ActorType`.

## Player UUIDs

```java
Set<UUID> players = BattleInspector.playerIds(battle);
boolean participating = BattleInspector.containsPlayer(battle, playerId);
```

Duplicate UUIDs are removed while preserving first-seen battle order.

## Pokémon

```java
List<Pokemon> all = BattleInspector.pokemon(battle);
List<Pokemon> wildPokemon = BattleInspector.pokemon(battle, ActorType.WILD);
```

Cobblemon currently stores battle participants in an internal `BattlePokemon`
wrapper. The library keeps that wrapper behind `BattleInspector` and exposes
normal `Pokemon` objects instead, giving consuming mods a smaller migration
surface when battle internals change.

## Initial battle weather

`BattleWeatherInitializer` lets a `BattleStartedEvent.Pre` subscriber set one
initial Showdown field weather before player entry and startup abilities run:

```java
if (BattleWeatherInitializer.isAvailable()) {
    boolean queued = BattleWeatherInitializer.setInitialWeather(
            event, InitialBattleWeather.RAIN);
}
```

The initializer is available only with Cobblemon `1.8.2+1.21.1`, where the
version-specific bridge is verified. It returns `false` on other versions or
when the runtime bridge did not install. The ambient condition is applied once
as a source-less field baseline; the simulator remains authoritative after
startup, and the library does not reapply ambient weather after moves or
abilities change it.

## Battle events

The inspector also unwraps common event payloads so feature code does not need
to depend directly on Cobblemon's internal `BattlePokemon` wrapper.

Fainted Pokémon:

```java
Pokemon defeated = BattleInspector.faintedPokemon(event);
```

Victory participants:

```java
Set<UUID> winners = BattleInspector.winningPlayerIds(event);
Set<UUID> losers = BattleInspector.losingPlayerIds(event);

boolean won = BattleInspector.didPlayerWin(event, playerId);
```

Cobblemon 1.8.2 also provides `BattleEndedEvent`, which fires for every battle
conclusion. Its `BattleEndResult` distinguishes `VICTORY`, `CAPTURE`,
`FORFEIT`, `FLED` and `STOPPED`. Winner and loser snapshots are available for
victory, capture and forfeit results:

```java
List<BattleActor> winners = BattleInspector.winners(endedEvent);
Set<UUID> winningPlayers = BattleInspector.winningPlayerIds(endedEvent);
boolean didWin = BattleInspector.didPlayerWin(endedEvent, playerId);
```

Fleeing and stopped battles have no winner or loser actors.

Normal `Pokemon` instances can also be extracted from any battle-actor
collection:

```java
List<Pokemon> defeatedSide =
        BattleInspector.pokemon(event.getLosers());
```

This is intended for KO chains, progression, rewards, statistics and similar
features while keeping Cobblemon battle internals behind the library.
