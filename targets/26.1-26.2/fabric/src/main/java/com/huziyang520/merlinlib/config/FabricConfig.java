// Wiring of the configuration screens on Fabric: Forge Config API Port is the only provider now.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.tools.ui.ConfigScreens;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public final class FabricConfig {
    private static final String PORT_API = "fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry";
    private static boolean installed;

    private FabricConfig() {
    }

    public static boolean install() {
        if (installed) {
            return true;
        }
        installed = true;
        if (!portPresent()) {
            ConfigManager.setBackend(new TomlBackend("plain"));
            Constants.LOG.info("[MerlinLib] Forge Config API Port is missing: client.toml is read directly and "
                    + "/merlinlib config cannot open a screen");
            return false;
        }
        FcapConfigs.install();
        // The port loads its files after the client entrypoint, so the old TOML is imported only once the
        // spec is loaded; writing before that would move the file without carrying its values across.
        ModConfigEvents.loading(Constants.MOD_ID).register(FabricConfig::adopt);
        ModConfigEvents.reloading(Constants.MOD_ID).register(FabricConfig::adopt);
        if (FcapConfigs.clientLoaded()) {
            ConfigManager.setBackend(FcapConfigs.clientBackend());
        }
        if (FcapConfigs.commonLoaded()) {
            ConfigManager.setServerBackend(FcapConfigs.commonBackend());
        }
        ConfigScreens.install(parent -> new ConfigurationScreen(Constants.MOD_ID, parent));
        Constants.LOG.info("[MerlinLib] Forge Config API Port found: the configuration screen is the NeoForge one");
        return true;
    }

    private static void adopt(ModConfig config) {
        if (config.getType() == ModConfig.Type.CLIENT) {
            ConfigManager.setBackend(FcapConfigs.clientBackend());
        } else if (config.getType() == FcapConfigs.sharedType()) {
            ConfigManager.setServerBackend(FcapConfigs.commonBackend());
        }
    }

    private static boolean portPresent() {
        try {
            Class.forName(PORT_API);
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }
}
