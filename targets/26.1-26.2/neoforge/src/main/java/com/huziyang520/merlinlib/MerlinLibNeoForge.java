// NeoForge entrypoint: config files, the command tree and the enchanted book tab entry.

package com.huziyang520.merlinlib;

import com.huziyang520.merlinlib.command.MerlinCommand;
import com.huziyang520.merlinlib.config.ConfigManager;
import com.huziyang520.merlinlib.config.NeoConfigs;
import com.huziyang520.merlinlib.tools.MerlinCreativeEntries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class MerlinLibNeoForge {
    public MerlinLibNeoForge(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::onBuildCreativeTab);
        eventBus.addListener(this::onConfigLoading);
        eventBus.addListener(this::onConfigReloading);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoConfigs.register(modContainer);
        MerlinLib.init(eventBus);
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        adopt(event.getConfig());
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        adopt(event.getConfig());
    }

    private static void adopt(ModConfig config) {
        if (config.getType() == ModConfig.Type.CLIENT) {
            ConfigManager.setBackend(NeoConfigs.clientBackend());
        } else if (config.getType() == NeoConfigs.sharedType()) {
            ConfigManager.setServerBackend(NeoConfigs.commonBackend());
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        MerlinCommand.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!MerlinCreativeEntries.TAB.equals(event.getTabKey())) {
            return;
        }
        for (ItemStack stack : MerlinCreativeEntries.enchantedBooks(event.getParameters().holders())) {
            if (event.getParentEntries().contains(stack) || event.getSearchEntries().contains(stack)) {
                continue;
            }
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
