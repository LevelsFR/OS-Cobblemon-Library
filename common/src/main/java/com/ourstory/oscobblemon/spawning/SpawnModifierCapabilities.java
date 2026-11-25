package com.ourstory.oscobblemon.spawning;

import java.util.Set;

/**
 * Runtime capabilities of the Cobblemon spawn-weight integration.
 *
 * @param available whether the native detector is installed for this Cobblemon version
 * @param cobblemonVersion the version reported by Cobblemon, or empty before initialization
 * @param supportedScopes scopes available to this runtime
 * @param formResolution whether native species and form data can be used by filters
 * @param unavailableReason why registration is disabled, or an empty string when available
 */
public record SpawnModifierCapabilities(
        boolean available,
        String cobblemonVersion,
        Set<SpawnModifierScope.Kind> supportedScopes,
        boolean formResolution,
        String unavailableReason
) {
    public SpawnModifierCapabilities {
        cobblemonVersion = cobblemonVersion == null ? "" : cobblemonVersion;
        supportedScopes = Set.copyOf(supportedScopes);
        unavailableReason = unavailableReason == null ? "" : unavailableReason;
    }

    public boolean supports(SpawnModifierScope.Kind scope) {
        return available && supportedScopes.contains(scope);
    }

    public boolean supportsPlayerContext() {
        return supports(SpawnModifierScope.Kind.PLAYER);
    }
}
