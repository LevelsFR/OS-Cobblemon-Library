package com.ourstory.oscobblemon.pokedex;

import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;

import java.util.Objects;

/**
 * Read-only access to the Pokédex data synchronized to the current client.
 * Use these methods only from client-side code.
 */
public final class ClientPokedexQueries {
    private ClientPokedexQueries() {
    }

    /** Returns Cobblemon's current client Pokédex manager. */
    public static ClientPokedexManager manager() {
        return CobblemonClient.INSTANCE.getClientPokedexData();
    }

    /** Copies the current client Pokédex state into an immutable snapshot. */
    public static PokedexSnapshot snapshot() {
        return snapshot(manager());
    }

    /** Copies the supplied client manager into an immutable snapshot. */
    public static PokedexSnapshot snapshot(ClientPokedexManager manager) {
        return PokedexQueries.snapshot(Objects.requireNonNull(manager, "manager"));
    }
}
