package com.huziyang520.merlinlib.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

/**
 * Declares MerlinLib's switches as two NeoForge configurations - the client half and the shared half -
 * and hands them to the mod container, which writes the files and shows the editing screen.
 */
public final class NeoConfigs {

    /** Loader 12 renamed the shared configuration type; resolved once, never referenced as a constant. */
    private static final ModConfig.Type SHARED_TYPE = resolveSharedType();

    private static final NeoSpecBackend CLIENT = NeoSpecBackend.declare("neoforge client", NeoConfigs::client);
    private static final NeoSpecBackend COMMON = NeoSpecBackend.declare("neoforge common", NeoConfigs::common);

    private NeoConfigs() {
    }

    /**
     * Finds the type the shared switches are registered under on this loader.
     *
     * @return {@code COMMON} when the loader still declares it, {@code LOCAL} otherwise
     */
    private static ModConfig.Type resolveSharedType() {
        try {
            return ModConfig.Type.valueOf("COMMON");
        } catch (IllegalArgumentException removedOnThisLoader) {
            // The shared, unsynced half of a mod's configuration is called LOCAL.
            return ModConfig.Type.valueOf("LOCAL");
        }
    }

    /** @return the resolved type of the shared configuration, for the load listener to recognise it */
    public static ModConfig.Type sharedType() {
        return SHARED_TYPE;
    }

    /**
     * Binds both specs to the mod.
     *
     * @param container the mod container handed to the entrypoint
     */
    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT.spec());
        container.registerConfig(SHARED_TYPE, COMMON.spec());
    }

    /** @return the backend of the client half */
    public static ConfigBackend clientBackend() {
        return CLIENT;
    }

    /** @return the backend of the common half */
    public static ConfigBackend commonBackend() {
        return COMMON;
    }

    /**
     * Declares the client switches, in the order {@code client.toml} documents them. The file layout
     * version is deliberately not here: it is written and read by the mod itself, and a key the screen
     * shows but nothing can meaningfully change only invites edits.
     *
     * @param spec the backend under construction
     */
    private static void client(NeoSpecBackend spec) {
                        spec.push("particles")
                .bool("limit_enabled", true,
                        "Cap how many particles this client draws, so a single huge hit cannot freeze it.")
                .number("limit", 512, 1, 1000000, "How many particles may be drawn per 1/20 second.")
                .pop();

                    }

    /**
     * Declares the server switches, in the order {@code server.toml} documents them.
     *
     * @param spec the backend under construction
     */
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
