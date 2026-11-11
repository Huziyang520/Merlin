// NeoForge client only entrypoint: the native configuration screen and the join notice page.

package com.huziyang520.merlinlib.client;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.tools.ui.ConfigScreens;
import com.huziyang520.merlinlib.tools.ui.MerlinScreens;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class MerlinLibNeoForgeClient {
    public MerlinLibNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        IConfigScreenFactory factory = (mod, parent) -> new ConfigurationScreen(mod, parent);
        modContainer.<IConfigScreenFactory>registerExtensionPoint(IConfigScreenFactory.class, () -> factory);
        ConfigScreens.install(parent -> new ConfigurationScreen(modContainer, parent));
        NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(Constants.MOD_ID)
                .then(Commands.literal("config")
                        .executes(context -> {
                            MerlinScreens.openConfigScreen(null);
                            return 1;
                        }))
                .then(Commands.literal("notices")
                        .executes(context -> {
                            MerlinScreens.openNoticesScreen(null);
                            return 1;
                        })));
    }
}
