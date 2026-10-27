package com.ourstory.oscobblemon.internal.mixin;

import com.ourstory.oscobblemon.internal.BattleWeatherHookStatus;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Loads the Showdown bridge only for the Cobblemon build it was verified on. */
public final class BattleWeatherMixinPlugin implements IMixinConfigPlugin {
    private static final Set<String> COBBLEMON_VERSIONS = Set.of("1.8.2+1.21.1", "1.8.3+1.21.1", "1.8.4+1.21.1");

    @Override
    public void onLoad(String mixinPackage) {
        String detectedVersion = findCobblemonVersion();
        boolean supported = COBBLEMON_VERSIONS.contains(detectedVersion);
        BattleWeatherHookStatus.configure(supported);
        if (!supported) {
            System.getLogger("os_cobblemon_library").log(
                    System.Logger.Level.WARNING,
                    "Initial battle weather hook disabled; verified Cobblemon version is "
                            + COBBLEMON_VERSIONS + ", detected "
                            + (detectedVersion == null ? "unknown" : detectedVersion)
            );
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return BattleWeatherHookStatus.isSupportedVersion();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }

    @Override
    public void postApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }

    private static String findCobblemonVersion() {
        String fabricVersion = findFabricVersion();
        if (fabricVersion != null) {
            return fabricVersion;
        }
        return findNeoForgeVersion();
    }

    private static String findFabricVersion() {
        try {
            Class<?> loaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object loader = loaderClass.getMethod("getInstance").invoke(null);
            Object result = loaderClass.getMethod("getModContainer", String.class)
                    .invoke(loader, "cobblemon");
            Object container = ((Optional<?>) result).orElse(null);
            if (container == null) {
                return null;
            }
            Class<?> containerApi = Class.forName("net.fabricmc.loader.api.ModContainer");
            Object metadata = containerApi.getMethod("getMetadata").invoke(container);
            Class<?> metadataApi = Class.forName("net.fabricmc.loader.api.metadata.ModMetadata");
            Object version = metadataApi.getMethod("getVersion").invoke(metadata);
            Class<?> versionApi = Class.forName("net.fabricmc.loader.api.metadata.version.Version");
            return String.valueOf(versionApi.getMethod("getFriendlyString").invoke(version));
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return null;
        }
    }

    private static String findNeoForgeVersion() {
        try {
            Class<?> loaderClass = Class.forName("net.neoforged.fml.loading.FMLLoader");
            Object loadingModList = loaderClass.getMethod("getLoadingModList").invoke(null);
            if (loadingModList == null) {
                return null;
            }
            Class<?> loadingListApi = Class.forName("net.neoforged.fml.loading.LoadingModList");
            Object mods = loadingListApi.getMethod("getMods").invoke(loadingModList);
            if (!(mods instanceof Iterable<?> modInfos)) {
                return null;
            }
            Class<?> modInfoApi = Class.forName("net.neoforged.neoforgespi.language.IModInfo");
            Method getModId = modInfoApi.getMethod("getModId");
            Method getVersion = modInfoApi.getMethod("getVersion");
            for (Object modInfo : modInfos) {
                if ("cobblemon".equals(getModId.invoke(modInfo))) {
                    return String.valueOf(getVersion.invoke(modInfo));
                }
            }
            return null;
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return null;
        }
    }
}
