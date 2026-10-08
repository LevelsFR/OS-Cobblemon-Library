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

## Codec-backed values

For a typed value, pass a Minecraft codec. Missing sections return the supplied
default; invalid stored data remains a `DataResult` error for the caller to
handle:

```java
DataResult<Integer> attempts = PokemonData.readOrDefault(
        pokemon, "examplemod:attempts", Codec.INT, 0);

DataResult<Integer> saved = PokemonData.write(
        pokemon, "examplemod:attempts", Codec.INT, 3);
```

The typed methods store the encoded value under the section's `value` field and
notify Cobblemon after a successful write. Keep a typed section separate from
the compound fields managed by `update(...)`. The default operations are
`NbtOps`; use the overload accepting `DynamicOps<Tag>` for codecs that need
registry-aware operations. Failed encodes leave Pokémon data unchanged.
