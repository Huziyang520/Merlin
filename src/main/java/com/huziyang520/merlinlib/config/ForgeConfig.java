// Wiring of the configuration screen on Forge: the NeoForge-style screen when its port is installed,
// Jupiter otherwise. The switch table itself lives in ClientOptions/ServerOptions.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.tools.ui.ConfigScreens;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ForgeConfig {
    private static final String NCP_REGISTRATION =
            "com.huziyang.neoconfigurationscreenport.forge.ConfigScreenRegistration";
    private static final String JUPITER_API = "com.iafenvoy.jupiter.ConfigManager";

    private static boolean installed;

    private ForgeConfig() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        if (ncpPresent()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SpecConfig.CLIENT_SPEC);
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SpecConfig.SERVER_SPEC);
            registerWithPort();
            Constants.LOG.info("[MerlinLib] Neo Configuration Screen Port found: the configuration screen edits "
                    + "client.toml and server.toml");
            return;
        }
        if (JupiterUi.present()) {
            JupiterUi.install();
            return;
        }
        Constants.LOG.info("[MerlinLib] neither the configuration screen port nor Jupiter is installed: "
                + "client.toml and server.toml are read directly and the mod list has no configuration screen");
    }

    public static boolean available() {
        return ConfigScreens.available();
    }

    /** True when the port owns the mod list's Config button, so the built-in screens stay out of the way. */
    public static boolean portInstalled() {
        return ncpPresent();
    }

    private static void registerWithPort() {
        try {
            Class<?> registration = Class.forName(NCP_REGISTRATION);
            registration.getMethod("register", String.class).invoke(null, Constants.MOD_ID);
        } catch (ReflectiveOperationException | LinkageError exception) {
            Constants.LOG.warn("[MerlinLib] the configuration screen port could not be hooked", exception);
        }
    }

    private static boolean ncpPresent() {
        return present(NCP_REGISTRATION);
    }

    private static boolean present(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }
}
