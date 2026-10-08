# Storage helpers

The `storage` package centralizes common player Party and PC access patterns.

## Party and PC access

```java
PlayerPartyStore party = PokemonStorage.party(player);
PCStore pc = PokemonStorage.pc(player);
```

UUID + `RegistryAccess` overloads are available when a `ServerPlayer` instance
is not available.

## Pokémon lookup

```java
Optional<Pokemon> partyPokemon = PokemonStorage.findInParty(player, pokemonId);
Optional<Pokemon> pcPokemon = PokemonStorage.findInPC(player, pokemonId);
Optional<Pokemon> owned = PokemonStorage.findOwned(player, pokemonId);
```

`findOwned` checks the party first and then the PC.

## Party slots

```java
OptionalInt slot = PokemonStorage.partySlot(player, pokemonId);
```

The returned slot is zero-based and empty when the Pokémon is not in the party.

## Snapshots

```java
List<Pokemon> party = PokemonStorage.partySnapshot(player);
List<Pokemon> owned = PokemonStorage.ownedSnapshot(player);
```

The returned lists are immutable snapshots of the store membership and ordering
at the time of the call. They still contain the live `Pokemon` objects.

Use the slot-preserving variant when the index must continue to match the party
slot. Its list has one entry per slot; empty positions are `Optional.empty()` and
occupied positions contain copied `PokemonSnapshot` values:

```java
List<Optional<PokemonSnapshot>> slots = PokemonStorage.partySlotsSnapshot(player);
```

## Client party

Client-only code can read Cobblemon's synchronized local party without casting
or accessing its mutable slot list directly:

```java
Optional<Pokemon> pokemon = ClientPokemonStorage.findInParty(pokemonId);
List<Pokemon> party = ClientPokemonStorage.partySnapshot();
List<Optional<PokemonSnapshot>> slots = ClientPokemonStorage.partySlotsSnapshot();
```

Overloads accept a `ClientParty` when the caller already has a store. The
returned list is an immutable membership snapshot containing live `Pokemon`
objects; use `PokemonSnapshot.from(pokemon)` when a cache should retain copied
display values instead. `partySlotsSnapshot` preserves every slot and returns
copied values. These helpers are client-only and must not be called from
dedicated-server code.

## Change notifications and cache lifetime

Storage reads are synchronous and do not cache. Server `PokemonStore` instances
expose a change observable; subscribe through the library when a store may
change outside the current call path:

```java
ObservableSubscription<Unit> changes = PokemonStorage.onPokemonStorageChanged(
        party, changedStore -> invalidateSnapshot(changedStore.getUuid()));
```

It emits for save-worthy changes to the store or its Pokémon. The callback
receives the store, so its concrete type identifies Party versus PC, but
Cobblemon does not include the changed Pokémon UUID. Compare a previous and
fresh snapshot when a consumer needs to identify membership changes. Remember
to unsubscribe when the cache or feature is discarded.

Client `ClientParty` updates use packet handlers and expose no matching
observable. Cobblemon's gain and release events remain useful signals but are
not a complete client-side invalidation API.

## Threading

These helpers do not add asynchronous storage access or caching. Use them under
the same threading rules as Cobblemon's storage API.

## Matcher and predicate queries

Storage helpers accept Java `Predicate<Pokemon>` values, so they work directly
with `PokemonMatcher`:

```java
PokemonMatcher matcher =
        PokemonMatcher.fromProperties("species=pikachu shiny=true")
                .alpha(true);

Optional<Pokemon> first =
        PokemonStorage.findFirstOwned(player, matcher);

List<Pokemon> all =
        PokemonStorage.findAllOwned(player, matcher);

boolean hasMatch =
        PokemonStorage.anyOwned(player, matcher);
```

Party-only variants preserve party slot order. Owned queries check or return the
party first and the PC second.

## Counting matches

For progression checks, quests or unlock conditions, count without creating an
intermediate list:

```java
int partyCount = PokemonStorage.countInParty(player, matcher);
int pcCount = PokemonStorage.countInPC(player, matcher);
int total = PokemonStorage.countOwned(player, matcher);
```

Predicate lookups are available consistently for Party, PC and combined owned
storage.
