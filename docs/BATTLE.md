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

Duplicate UUIDs are removed while preserving deterministic iteration before the
immutable result is created.

## Pokémon

```java
List<Pokemon> all = BattleInspector.pokemon(battle);
List<Pokemon> wildPokemon = BattleInspector.pokemon(battle, ActorType.WILD);
```

Cobblemon currently stores battle participants in an internal `BattlePokemon`
wrapper. The library keeps that wrapper behind `BattleInspector` and exposes
normal `Pokemon` objects instead, giving consuming mods a smaller migration
surface when battle internals change.

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

Normal `Pokemon` instances can also be extracted from any battle-actor
collection:

```java
List<Pokemon> defeatedSide =
        BattleInspector.pokemon(event.getLosers());
```

This is intended for KO chains, progression, rewards, statistics and similar
features while keeping Cobblemon battle internals behind the library.
