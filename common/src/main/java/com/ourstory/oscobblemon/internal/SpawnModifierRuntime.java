package com.ourstory.oscobblemon.internal;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence;
import com.cobblemon.mod.common.api.spawning.influence.SpawningZoneInfluence;
import com.cobblemon.mod.common.api.spawning.influence.UnconditionalSpawningZoneInfluence;
import com.cobblemon.mod.common.api.spawning.influence.detector.SpawningInfluenceDetector;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner;
import com.cobblemon.mod.common.api.spawning.spawner.Spawner;
import com.cobblemon.mod.common.api.spawning.spawner.SpawningZoneInput;
import com.ourstory.oscobblemon.spawning.SpawnCandidate;
import com.ourstory.oscobblemon.spawning.SpawnFilter;
import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierCapabilities;
import com.ourstory.oscobblemon.spawning.SpawnModifierHandle;
import com.ourstory.oscobblemon.spawning.SpawnModifierScope;
import com.ourstory.oscobblemon.spawning.SpawnModifierSnapshot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Cobblemon engine bridge and server-local modifier storage. */
public final class SpawnModifierRuntime {
    private static final Set<String> SUPPORTED_COBBLEMON_VERSIONS = Set.of("1.8.2", "1.8.3", "1.8.4");
    private static final SpawnModifierSnapshot EMPTY_SNAPSHOT = new SpawnModifierSnapshot(List.of());
    private static final Object REGISTRIES_LOCK = new Object();
    private static final Map<MinecraftServer, SpawnModifierRegistry> REGISTRIES = new java.util.WeakHashMap<>();
    private static final Detector DETECTOR = new Detector();

    private static volatile boolean initialized;
    private static volatile boolean available;
    private static volatile String cobblemonVersion = "";
    private static volatile String unavailableReason = "Spawn influence detector has not been installed";

    private SpawnModifierRuntime() {
    }

    /** Installs the native detector once. Called by the shared OSCL initializer. */
    public static synchronized void initialize() {
        if (initialized) return;
        initialized = true;
        try {
            cobblemonVersion = Cobblemon.VERSION;
            String versionLine = cobblemonVersion;
            int suffix = versionLine.indexOf('+');
            if (suffix < 0) suffix = versionLine.indexOf('-');
            if (suffix >= 0) versionLine = versionLine.substring(0, suffix);
            if (!SUPPORTED_COBBLEMON_VERSIONS.contains(versionLine)) {
                unavailableReason = "Unsupported Cobblemon version " + cobblemonVersion
                        + "; supported versions are 1.8.2, 1.8.3 and 1.8.4";
                System.getLogger("os_cobblemon_library").log(System.Logger.Level.WARNING, unavailableReason);
                return;
            }
            SpawningInfluenceDetector.getDetectors().add(DETECTOR);
            available = true;
            unavailableReason = "";
            System.getLogger("os_cobblemon_library").log(
                    System.Logger.Level.INFO,
                    "Registered Cobblemon natural spawn-weight influence detector"
            );
        } catch (LinkageError | RuntimeException exception) {
            available = false;
            unavailableReason = exception.getClass().getSimpleName() + ": " + String.valueOf(exception.getMessage());
            System.getLogger("os_cobblemon_library").log(
                    System.Logger.Level.WARNING,
                    "Spawn Modifier API is unavailable; native Cobblemon detector registration failed: "
                            + unavailableReason
            );
        }
    }

    public static SpawnModifierCapabilities capabilities() {
        Set<SpawnModifierScope.Kind> scopes = available
                ? EnumSet.allOf(SpawnModifierScope.Kind.class)
                : Set.of();
        return new SpawnModifierCapabilities(available, cobblemonVersion, scopes, available, unavailableReason);
    }

    public static SpawnModifierHandle register(MinecraftServer server, SpawnModifier modifier, boolean replace) {
        requireAvailable();
        SpawnModifierRegistry registry = registry(server, true);
        long generation = registry.register(server.getTickCount(), modifier, replace);
        return new RegistrationHandle(server, registry, modifier.id(), generation);
    }

    public static boolean remove(MinecraftServer server, ResourceLocation id) {
        SpawnModifierRegistry registry = registry(server, false);
        return registry != null && registry.remove(server.getTickCount(), id);
    }

    public static int removeSource(MinecraftServer server, ResourceLocation source) {
        SpawnModifierRegistry registry = registry(server, false);
        return registry == null ? 0 : registry.removeSource(server.getTickCount(), source);
    }

    public static Optional<SpawnModifier> find(MinecraftServer server, ResourceLocation id) {
        SpawnModifierRegistry registry = registry(server, false);
        return registry == null ? Optional.empty() : registry.find(server.getTickCount(), id);
    }

    public static SpawnModifierSnapshot snapshot(MinecraftServer server) {
        if (!available) return EMPTY_SNAPSHOT;
        SpawnModifierRegistry registry = registry(server, false);
        return registry == null ? EMPTY_SNAPSHOT : registry.snapshot(server.getTickCount());
    }

    /** Clears one integrated or dedicated server without affecting other servers in this process. */
    public static void serverStopped(MinecraftServer server) {
        SpawnModifierRegistry registry;
        synchronized (REGISTRIES_LOCK) {
            registry = REGISTRIES.remove(server);
        }
        if (registry != null) registry.clear();
    }

    private static void requireAvailable() {
        if (!available) {
            throw new IllegalStateException("Cobblemon spawn-weight integration is unavailable: " + unavailableReason);
        }
    }

    private static SpawnModifierRegistry registry(MinecraftServer server, boolean create) {
        if (server == null) throw new NullPointerException("server");
        synchronized (REGISTRIES_LOCK) {
            SpawnModifierRegistry registry = REGISTRIES.get(server);
            if (registry == null && create) {
                registry = new SpawnModifierRegistry();
                REGISTRIES.put(server, registry);
            }
            return registry;
        }
    }

    private static final class Detector implements SpawningInfluenceDetector {
        @Override
        public List<SpawningZoneInfluence> detectFromInput(Spawner spawner, SpawningZoneInput input) {
            if (!(spawner instanceof PlayerSpawner playerSpawner)) return List.of();

            MinecraftServer server = input.getWorld().getServer();
            SpawnModifierSnapshot snapshot = SpawnModifierRuntime.snapshot(server);
            if (snapshot.modifiers().isEmpty()) return List.of();

            ResourceLocation dimension = input.getWorld().dimension().location();
            UUID playerId = playerSpawner.getUuid();
            List<SpawnModifier> scoped = new ArrayList<>();
            for (SpawnModifier modifier : snapshot.modifiers()) {
                if (modifier.scope().matches(dimension, playerId)) scoped.add(modifier);
            }
            if (scoped.isEmpty()) return List.of();

            WeightInfluence influence = new WeightInfluence(scoped);
            return List.of(new UnconditionalSpawningZoneInfluence(influence));
        }

        @Override
        public List<SpawningZoneInfluence> detectFromBlock(
                net.minecraft.server.level.ServerLevel world,
                net.minecraft.core.BlockPos pos,
                net.minecraft.world.level.block.state.BlockState blockState
        ) {
            return List.of();
        }
    }

    private static final class WeightInfluence implements SpawningInfluence {
        private final List<SpawnModifier> modifiers;
        private final Map<SpawnDetail, Double> logMultiplierCache = Collections.synchronizedMap(new IdentityHashMap<>());

        private WeightInfluence(List<SpawnModifier> modifiers) {
            this.modifiers = List.copyOf(modifiers);
        }

        @Override
        public float affectWeight(SpawnDetail detail, SpawnablePosition position, float weight) {
            if (!(weight > 0.0F) || !Float.isFinite(weight) || detail.getPercentage() > 0.0F) return weight;

            double logMultiplier;
            synchronized (logMultiplierCache) {
                Double cached = logMultiplierCache.get(detail);
                if (cached != null) {
                    logMultiplier = cached;
                } else {
                    logMultiplier = computeLogMultiplier(detail);
                    logMultiplierCache.put(detail, logMultiplier);
                }
            }
            return SpawnModifierEvaluator.apply(weight, detail.getPercentage(), logMultiplier);
        }

        private double computeLogMultiplier(SpawnDetail detail) {
            List<SpawnCandidate> candidates = SpawnCandidateData.from(detail);
            return SpawnModifierEvaluator.logMultiplier(modifiers, candidates);
        }
    }

    private static final class RegistrationHandle implements SpawnModifierHandle {
        private final WeakReference<MinecraftServer> server;
        private final SpawnModifierRegistry registry;
        private final ResourceLocation id;
        private final long generation;

        private RegistrationHandle(MinecraftServer server, SpawnModifierRegistry registry, ResourceLocation id, long generation) {
            this.server = new WeakReference<>(server);
            this.registry = registry;
            this.id = id;
            this.generation = generation;
        }

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public boolean isActive() {
            MinecraftServer current = server.get();
            return current != null && registry.isActive(current.getTickCount(), id, generation);
        }

        @Override
        public boolean remove() {
            MinecraftServer current = server.get();
            return current != null && registry.remove(current.getTickCount(), id, generation);
        }
    }
}
