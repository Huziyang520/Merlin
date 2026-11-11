// Declares MerlinLib's switches as two NeoForge configurations - the client half and the shared half - and hands them to ...

package com.huziyang520.merlinlib.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public final class NeoConfigs {
    private static final ModConfig.Type SHARED_TYPE = resolveSharedType();
    private static final NeoSpecBackend CLIENT = NeoSpecBackend.declare("neoforge client", NeoConfigs::client);
    private static final NeoSpecBackend COMMON = NeoSpecBackend.declare("neoforge common", NeoConfigs::common);

    private NeoConfigs() {
    }

    private static ModConfig.Type resolveSharedType() {
        try {
            return ModConfig.Type.valueOf("COMMON");
        } catch (IllegalArgumentException removedOnThisLoader) {
            return ModConfig.Type.valueOf("LOCAL");
        }
    }

    public static ModConfig.Type sharedType() {
        return SHARED_TYPE;
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT.spec());
        container.registerConfig(SHARED_TYPE, COMMON.spec());
    }

    public static ConfigBackend clientBackend() {
        return CLIENT;
    }

    public static ConfigBackend commonBackend() {
        return COMMON;
    }

    private static void client(NeoSpecBackend spec) {
        
        
        spec.push("particles")
                .bool("limit_enabled", true,
                        "Cap how many particles this client draws, so a single huge hit cannot freeze it.")
                .number("limit", 512, 1, 1000000, "How many particles may be drawn per 1/20 second.")
                .pop();

        
            }

    private static void common(NeoSpecBackend spec) {
        
        
        spec.push("content")
                .bool("disable_generated_enchantments", false,
                        "Generate none of the enchantments defined in config/MerlinLib/*.json.")
                .pop();

        spec.push("notices")
                .bool("enabled", true, "Show the join notices business mods registered through MerlinLib.")
                .pop();
    }
}
