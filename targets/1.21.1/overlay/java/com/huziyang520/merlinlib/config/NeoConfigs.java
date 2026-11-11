// Declares MerlinLib's switches as two NeoForge configurations - the client half and the shared half - and hands them to ...

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public final class NeoConfigs {
    public static final String CLIENT_FILE = Constants.MOD_ID + "-client.toml";
    public static final String COMMON_FILE = Constants.MOD_ID + "-common.toml";
    private static final String LEGACY_CLIENT_FILE = "client.toml";
    private static final String LEGACY_COMMON_FILE = "server.toml";

    private static final NeoSpecBackend CLIENT =
            NeoSpecBackend.declare("neoforge client", CLIENT_FILE, LEGACY_CLIENT_FILE, NeoConfigs::client);
    private static final NeoSpecBackend COMMON =
            NeoSpecBackend.declare("neoforge common", COMMON_FILE, LEGACY_COMMON_FILE, NeoConfigs::common);

    private NeoConfigs() {
    }

    public static ModConfig.Type sharedType() {
        return ModConfig.Type.COMMON;
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT.spec(), CLIENT_FILE);
        container.registerConfig(sharedType(), COMMON.spec(), COMMON_FILE);
    }

    public static ConfigBackend clientBackend() {
        return CLIENT;
    }

    public static ConfigBackend commonBackend() {
        return COMMON;
    }

    static NeoSpecBackend clientSpec() {
        return CLIENT;
    }

    static NeoSpecBackend commonSpec() {
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
