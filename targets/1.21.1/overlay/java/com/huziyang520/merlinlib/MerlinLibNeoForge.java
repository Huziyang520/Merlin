// NeoForge entrypoint.

package com.huziyang520.merlinlib;

import com.huziyang520.merlinlib.command.MerlinCommand;
import com.huziyang520.merlinlib.config.ConfigManager;
import com.huziyang520.merlinlib.config.ConfigScope;
import com.huziyang520.merlinlib.config.NeoConfigs;
import com.huziyang520.merlinlib.config.NoticeSettings;
import com.huziyang520.merlinlib.tools.MerlinCreativeEntries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class MerlinLibNeoForge {
    public MerlinLibNeoForge(IEventBus eventBus, ModContainer modContainer) {
        NoticeSettings.init();
        NeoConfigs.register(modContainer);
        ConfigManager.setBackend(ConfigScope.CLIENT, NeoConfigs.clientBackend());
        ConfigManager.setBackend(ConfigScope.SERVER, NeoConfigs.commonBackend());
        eventBus.addListener(this::onConfigLoading);
        eventBus.addListener(this::onConfigReloading);
        eventBus.addListener(this::onBuildCreativeTab);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MerlinLib.init(eventBus);
    }

    private void onConfigLoading(ModConfigEvent.Loading event) {
        ConfigManager.onConfigLoaded(event.getConfig());
    }

    private void onConfigReloading(ModConfigEvent.Reloading event) {
        ConfigManager.reload();
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
