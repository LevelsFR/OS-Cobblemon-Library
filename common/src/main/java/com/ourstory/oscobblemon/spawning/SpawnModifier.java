package com.ourstory.oscobblemon.spawning;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Immutable weight modifier registered against one Minecraft server. */
public final class SpawnModifier {
    public static final double MIN_MULTIPLIER = 0.000001;
    public static final double MAX_MULTIPLIER = 1_000_000.0;

    private static final ResourceLocation DEFAULT_GROUP = ResourceLocation.fromNamespaceAndPath(
            "os_cobblemon_library", "default"
    );

    private final ResourceLocation id;
    private final ResourceLocation source;
    private final SpawnModifierScope scope;
    private final SpawnFilter filter;
    private final double multiplier;
    private final SpawnStackingPolicy stackingPolicy;
    private final long durationTicks;

    private SpawnModifier(Builder builder) {
        id = builder.id;
        source = builder.source;
        scope = builder.scope;
        filter = builder.filter;
        multiplier = builder.multiplier;
        stackingPolicy = builder.stackingPolicy;
        durationTicks = builder.durationTicks;
    }

    public static Builder builder(ResourceLocation id, ResourceLocation source) {
        return new Builder(id, source);
    }

    public ResourceLocation id() {
        return id;
    }

    public ResourceLocation source() {
        return source;
    }

    public SpawnModifierScope scope() {
        return scope;
    }

    public SpawnFilter filter() {
        return filter;
    }

    public double multiplier() {
        return multiplier;
    }

    public SpawnStackingPolicy stackingPolicy() {
        return stackingPolicy;
    }

    /** Returns zero for a permanent modifier; otherwise its server-tick lifetime. */
    public long durationTicks() {
        return durationTicks;
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final ResourceLocation source;
        private SpawnModifierScope scope = SpawnModifierScope.global();
        private SpawnFilter filter = SpawnFilter.any();
        private double multiplier = 1.0;
        private SpawnStackingPolicy stackingPolicy = SpawnStackingPolicy.max(DEFAULT_GROUP);
        private long durationTicks;

        private Builder(ResourceLocation id, ResourceLocation source) {
            this.id = Objects.requireNonNull(id, "id");
            this.source = Objects.requireNonNull(source, "source");
        }

        public Builder scope(SpawnModifierScope scope) {
            this.scope = Objects.requireNonNull(scope, "scope");
            return this;
        }

        public Builder filter(SpawnFilter filter) {
            this.filter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        /** A finite positive multiplier; zero is deliberately unsupported. */
        public Builder multiplier(double multiplier) {
            if (!Double.isFinite(multiplier) || multiplier < MIN_MULTIPLIER || multiplier > MAX_MULTIPLIER) {
                throw new IllegalArgumentException("multiplier must be finite and in [1e-6, 1e6]");
            }
            this.multiplier = multiplier;
            return this;
        }

        public Builder stackingPolicy(SpawnStackingPolicy stackingPolicy) {
            this.stackingPolicy = Objects.requireNonNull(stackingPolicy, "stackingPolicy");
            return this;
        }

        /** Sets a lifetime measured in server ticks; expiration pauses with server ticks. */
        public Builder expiresAfterTicks(long durationTicks) {
            if (durationTicks <= 0) {
                throw new IllegalArgumentException("durationTicks must be greater than zero");
            }
            this.durationTicks = durationTicks;
            return this;
        }

        public Builder permanent() {
            durationTicks = 0;
            return this;
        }

        public SpawnModifier build() {
            return new SpawnModifier(this);
        }
    }
}
