// Declares MerlinLib's two halves through Forge Config API Port, so the Fabric screen matches NeoForge.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;

public final class FcapConfigs {
    private static final ModConfig.Type SHARED_TYPE = resolveSharedType();
    private static final FcapSpecBackend CLIENT = FcapSpecBackend.declare("fabric client", FcapConfigs::client);
    private static final FcapSpecBackend COMMON = FcapSpecBackend.declare("fabric common", FcapConfigs::common);

    private FcapConfigs() {
    }

    private static ModConfig.Type resolveSharedType() {
        try {
            return ModConfig.Type.valueOf("COMMON");
        } catch (IllegalArgumentException removedOnThisLoader) {
            return ModConfig.Type.valueOf("SERVER");
        }
    }

    public static ModConfig.Type sharedType() {
        return SHARED_TYPE;
    }

    public static void install() {
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.CLIENT, CLIENT.spec());
        ConfigRegistry.INSTANCE.register(Constants.MOD_ID, SHARED_TYPE, COMMON.spec());
        Constants.LOG.info("[MerlinLib] configuration is served by Forge Config API Port");
    }

    public static ConfigBackend clientBackend() {
        return CLIENT;
    }

    public static ConfigBackend commonBackend() {
        return COMMON;
    }

    static boolean clientLoaded() {
        return CLIENT.spec().isLoaded();
    }

    static boolean commonLoaded() {
        return COMMON.spec().isLoaded();
    }

    private static void client(FcapSpecBackend spec) {
        spec.push("particles")
                .bool("limit_enabled", true,
                        "Cap how many particles this client draws, so a single huge hit cannot freeze it.")
                .number("limit", 512, 1, 1000000, "How many particles may be drawn per 1/20 second.")
                .pop();
    }

    private static void common(FcapSpecBackend spec) {
        spec.push("content")
                .bool("disable_generated_enchantments", false,
                        "Generate none of the enchantments defined in config/MerlinLib/*.json.")
                .pop();

        spec.push("notices")
                .bool("enabled", true, "Show the join notices business mods registered through MerlinLib.")
                .pop();
    }
}
