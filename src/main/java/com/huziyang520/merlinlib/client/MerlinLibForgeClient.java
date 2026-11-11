// Forge client entrypoint: the optional configuration screen and the join notice page.

package com.huziyang520.merlinlib.client;

import com.huziyang520.merlinlib.config.MezzConfigBridge;
import com.huziyang520.merlinlib.tools.ui.MerlinScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.huziyang520.merlinlib.Constants.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MerlinLibForgeClient {
    private MerlinLibForgeClient() {
    }

    public static void install() {
        MezzConfigBridge.install();
        com.huziyang520.merlinlib.config.ForgeConfig.install();
        MerlinScreens.installCommands();
    }
}
