// The Forge events MerlinLib subscribes to on the game event bus.

package com.huziyang520.merlinlib;

import com.huziyang520.merlinlib.command.MerlinCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MerlinGameEvents {
    private MerlinGameEvents() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        MerlinCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

}
