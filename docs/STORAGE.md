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
