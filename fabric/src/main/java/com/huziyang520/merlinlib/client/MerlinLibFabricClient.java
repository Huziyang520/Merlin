// Fabric client entrypoint: the optional configuration screen and the join notice page.

package com.huziyang520.merlinlib.client;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.config.FabricConfig;
import com.huziyang520.merlinlib.config.MezzConfigBridge;
import com.huziyang520.merlinlib.tools.ui.MerlinScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

public class MerlinLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (!FabricConfig.install()) {
            MezzConfigBridge.install();
        }

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> dispatcher.register(
                ClientCommands.literal(Constants.MOD_ID)
                        .then(ClientCommands.literal("config")
                                .executes(context -> {
                                    MerlinScreens.openConfigScreen(null);
                                    return 1;
                                }))
                        .then(ClientCommands.literal("notices")
                                .executes(context -> {
                                    MerlinScreens.openNoticesScreen(null);
                                    return 1;
                                }))));
    }
}
