# Pokémon display snapshots and Poké Balls

`PokemonSnapshot` captures values commonly needed by a UI without retaining a
live Pokémon reference:

```java
PokemonSnapshot snapshot = PokemonSnapshot.from(pokemon);
ResourceLocation ballId = snapshot.caughtBallId();
Component name = snapshot.displayName();
```

The snapshot includes UUID, species ID, normalized form ID, aspects, shiny and
Alpha state, caught Poké Ball ID, a copied display-name component, level, current
and maximum HP, and the persistent status ID. `persistentStatusId()` is `null`
when the Pokémon has no persistent status. Its set of aspects is immutable.
Each `displayName()` call returns a component copy. All values are captured at
the time `from` is called.

Resolve Poké Ball IDs and translated names from Cobblemon data or an item stack:

```java
ResourceLocation id = PokemonBalls.idOf(pokemon.getCaughtBall());
Component translatedName = PokemonBalls.displayName(pokemon.getCaughtBall());

Optional<ResourceLocation> stackBallId = PokemonBalls.idOf(itemStack);
Optional<Component> stackBallName = PokemonBalls.displayName(itemStack);
```

Stack overloads return an empty `Optional` when the stack is empty or does not
contain a Cobblemon `PokeBallItem`. IDs come from the registered Cobblemon ball
definition, so addon ball identifiers are preserved. Names are Minecraft text
components and resolve through the item's translation in the active client
language. The library does not provide icon rendering.
