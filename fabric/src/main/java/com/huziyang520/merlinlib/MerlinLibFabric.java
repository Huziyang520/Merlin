// Fabric entrypoint: the library bootstrap, the command tree and the enchanted book tab entry.

package com.huziyang520.merlinlib;

import com.huziyang520.merlinlib.command.MerlinCommand;
import com.huziyang520.merlinlib.tools.MerlinCreativeEntries;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MerlinLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(MerlinCommand::register);

        CreativeModeTabEvents.modifyOutputEvent(MerlinCreativeEntries.TAB).register(output -> {
            for (ItemStack stack : MerlinCreativeEntries.enchantedBooks(output.getContext().holders())) {
                output.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        });
        MerlinLib.init(null);
    }
}
