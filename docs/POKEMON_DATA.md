# Pokémon persistent data

`PokemonData` provides a small compatibility boundary around Cobblemon's
`Pokemon.persistentData` compound.

## Read and update

```java
PokemonData.update(pokemon, "examplemod:quest_state", data -> {
    data.putBoolean("completed", true);
    data.putInt("attempts", data.getInt("attempts") + 1);
});

boolean completed = PokemonData.read(pokemon, "examplemod:quest_state")
        .map(data -> data.getBoolean("completed"))
        .orElse(false);
```

Use a key owned by the consuming mod. The library does not reserve a global
schema or interpret the contents of the compound.

`read(...)` returns a defensive copy, so changing the returned tag does not
silently mutate the Pokémon. Use `update(...)` for writes; it replaces the
section and calls Cobblemon's `onChange(...)` notification. `remove(...)`
performs the same notification when a section exists.

This data is stored by Cobblemon with the Pokémon and therefore follows the
Pokémon through Party, PC and entity persistence. It is not player data and it
does not replace a mod's own configuration or world storage.
