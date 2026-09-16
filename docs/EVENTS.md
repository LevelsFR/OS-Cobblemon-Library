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

Current convenience hooks cover recurring areas such as capture, battles,
Pokédex updates, hatching, trade, release, nickname changes, level-up and
experience gain.

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
