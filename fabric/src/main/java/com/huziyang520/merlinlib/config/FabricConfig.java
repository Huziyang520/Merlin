// Wiring of the configuration screens on Fabric: Forge Config API Port is the only provider now.

package com.huziyang520.merlinlib.config;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.tools.ui.ConfigScreens;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public final class FabricConfig {
    private static final String PORT_API = "fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry";

    private FabricConfig() {
    }

    public static boolean install() {
        if (!portPresent()) {
            ConfigManager.setBackend(new TomlBackend("plain"));
            Constants.LOG.info("[MerlinLib] Forge Config API Port is missing: client.toml is read directly and "
                    + "/merlinlib config cannot open a screen");
            return false;
        }
        FcapConfigs.install();
        ConfigManager.setBackend(FcapConfigs.clientBackend());
        ConfigScreens.install(parent -> new ConfigurationScreen(Constants.MOD_ID, parent));
        Constants.LOG.info("[MerlinLib] Forge Config API Port found: the configuration screen is the NeoForge one");
        return true;
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
