package com.ourstory.oscobblemon.spawning;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Stable, read-only spawn-definition data used by {@link SpawnFilter}. */
public record SpawnCandidate(
        String speciesId,
        List<Form> possibleForms,
        Set<String> aspects,
        String bucket
) {
    public SpawnCandidate {
        Objects.requireNonNull(speciesId, "speciesId");
        if (!speciesId.contains(":")) {
            throw new IllegalArgumentException("speciesId must be namespaced");
        }
        possibleForms = List.copyOf(Objects.requireNonNull(possibleForms, "possibleForms"));
        if (possibleForms.isEmpty()) {
            throw new IllegalArgumentException("possibleForms must not be empty");
        }
        aspects = normalize(aspects, "aspects");
        bucket = Objects.requireNonNull(bucket, "bucket").trim().toLowerCase(Locale.ROOT);
        if (bucket.isEmpty()) {
            throw new IllegalArgumentException("bucket must not be blank");
        }
    }

    /** Attributes of one possible resolved form. The empty form ID means standard form. */
    public record Form(String id, Set<String> types, Set<String> labels) {
        public Form {
            id = Objects.requireNonNull(id, "id").trim().toLowerCase(Locale.ROOT);
            types = normalize(types, "types");
            labels = normalize(labels, "labels");
        }
    }

    private static Set<String> normalize(Set<String> values, String name) {
        Objects.requireNonNull(values, name);
        return values.stream()
                .map(value -> Objects.requireNonNull(value, name + " entry").trim().toLowerCase(Locale.ROOT))
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
