// Declares MerlinLib's switches as two Forge configurations - the client half and the shared half - so the
// NeoForge-style configuration screen can render and edit them. Both halves fall back to the toml files
// this mod used before, so the many switches those files carry keep working.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeSpecs {
    public static final String CLIENT_FILE = Constants.MOD_ID + "-client.toml";
    public static final String COMMON_FILE = Constants.MOD_ID + "-common.toml";

    private static final ForgeSpecBackend CLIENT = ForgeSpecBackend.declare("forge client", CLIENT_FILE,
            "client.toml", () -> TomlFile.read(ClientConfig.path(), ClientConfig.defaults()), ForgeSpecs::client);
    private static final ForgeSpecBackend COMMON = ForgeSpecBackend.declare("forge common", COMMON_FILE,
            "server.toml", () -> TomlFile.read(ServerConfig.path(), ServerConfig.defaults()), ForgeSpecs::common);

    private ForgeSpecs() {
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.spec(), CLIENT_FILE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.spec(), COMMON_FILE);
    }

    public static ConfigBackend clientBackend() {
        return CLIENT;
    }

    public static ConfigBackend commonBackend() {
        return COMMON;
    }

    private static void client(ForgeSpecBackend spec) {
        spec.push("particles")
                .bool("limit_enabled", true,
                        "Cap how many particles this client draws, so a single huge hit cannot freeze it.")
                .number("limit", 512, 1, 1000000, "How many particles may be drawn per 1/20 second.")
                .pop();
    }

    private static void common(ForgeSpecBackend spec) {
        spec.push("content")
                .bool("disable_generated_enchantments", false,
                        "Generate none of the enchantments defined in config/MerlinLib/*.json.")
                .pop();
        spec.push("notices")
                .bool("enabled", true, "Show the join notices business mods registered through MerlinLib.")
                .pop();
    }
}
