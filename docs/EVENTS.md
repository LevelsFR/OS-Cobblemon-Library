# Event helpers

The `event` package provides a small Java-friendly layer around Cobblemon
events that are commonly reused by mods.

It does not mirror every event exposed by Cobblemon. Direct
`CobblemonEvents` access remains appropriate for one-off or highly specialized
events.

## Common subscriptions

```java
ObservableSubscription<PokemonCapturedEvent> capture =
        CobblemonEventHooks.onCapture(event -> {
            // Handle capture.
        });

ObservableSubscription<BattleVictoryEvent> victory =
        CobblemonEventHooks.onBattleVictory(Priority.HIGH, event -> {
            // Handle battle victory.
        });
```

The capture payload already contains the caught Pokémon, player and thrown
Poké Ball entity. Read the ball definition with
`event.getPokeBallEntity().getPokeBall()`; no second capture event is needed.

Current convenience hooks cover recurring areas such as capture and capture
calculation, evolution, battle start and gimmicks, send/recall, healing,
Pokémon entity persistence, Pokémon spawning, Pokédex updates, hatching,
fossil revival, held and cosmetic item swaps, trade, release, nickname changes,
level-up and experience gain.

Held-item and cosmetic-item hooks each have pre and post forms. A pre handler
can cancel or adjust the receiving/returning stacks and decrement flag. A post
handler observes copies of the received and returned stacks; editing those
copies does not change the completed swap.

```java
ObservableSubscription<HeldItemEvent.Pre> heldItem =
        CobblemonEventHooks.onHeldItemPre(event -> {
            if (shouldBlock(event.getPokemon(), event.getReceiving())) {
                event.cancel();
            }
        });

ObservableSubscription<HeldItemEvent.Post> cosmeticItem =
        CobblemonEventHooks.onCosmeticItemPost(event -> {
            recordCosmeticSwap(event.getPokemon(), event.getReceived(), event.getReturned());
        });
```

`onBattleEnded` covers every battle conclusion. Use the result to distinguish
victory, capture, forfeit, fleeing and stops without a winner:

```java
ObservableSubscription<BattleEndedEvent> ended =
        CobblemonEventHooks.onBattleEnded(event -> {
            if (event.getResult() == BattleEndResult.CAPTURE) {
                // Handle a wild Pokémon capture ending the battle.
            }
        });
```

Cobblemon only populates the battle's winner and loser lists for victory,
capture and forfeit results. `BattleInspector` provides immutable snapshots
and player UUID queries for those outcomes.

Pre hooks expose Cobblemon's cancelable event object. Post hooks expose the
resulting event payload. Mutable Cobblemon payloads such as capture rate,
capture result, healing amount and event NBT remain available to the handler.

```java
ObservableSubscription<PokemonSentEvent.Pre> send =
        CobblemonEventHooks.onPokemonSentPre(event -> {
            if (shouldBlock(event.getPokemon())) {
                event.cancel();
            }
        });

ObservableSubscription<PokemonEntitySaveEvent> save =
        CobblemonEventHooks.onPokemonEntitySave(event -> {
            event.getNbt().putBoolean("examplemod:marked", true);
        });
```

The persistence and spawn hooks stay in the common module and do not import
client-only GUI types. Client-only GUI events are intentionally left to direct
Cobblemon access in the client module.

Pokédex pre/post and gain/release hooks wrap Cobblemon's native server events.
For a broader server cache signal, subscribe to the store observable described
in [storage cache guidance](STORAGE.md#change-notifications-and-cache-lifetime).
Client packet updates do not use these server event hooks.

## Subscription lifecycle

Cobblemon subscriptions should be cleaned up when their feature lifecycle ends.
Use `EventSubscriptionGroup` for reloadable systems or features that can be
enabled and disabled:

```java
EventSubscriptionGroup subscriptions = new EventSubscriptionGroup();

subscriptions.track(
        CobblemonEventHooks.onCapture(this::handleCapture)
);

subscriptions.track(
        CobblemonEventHooks.onBattleVictory(this::handleVictory)
);

// Later:
subscriptions.unsubscribeAll();
```

The group also implements `AutoCloseable`.

## Why this layer exists

The helpers provide:

- a stable place to absorb Cobblemon event field or type changes
- cleaner Java call sites
- consistent priority handling
- explicit subscription lifecycle management

They intentionally do not transform event payloads or hide useful Cobblemon
event data.
