// Supplies the creative inventory entry: every enchantment the library serves, as an enchanted book.

package com.huziyang520.merlinlib.tools;

import com.huziyang520.merlinlib.impl.ContentManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import java.util.ArrayList;
import java.util.List;

public final class MerlinCreativeEntries {
    public static final net.minecraft.resources.ResourceKey<CreativeModeTab> TAB =
            net.minecraft.world.item.CreativeModeTabs.INGREDIENTS;


    private MerlinCreativeEntries() {
    }

    public static List<ItemStack> enchantedBooks() {
        List<ItemStack> stacks = new ArrayList<>();
        ContentManager.current().enchantments().keySet().stream().sorted().forEach(id -> {
            Enchantment enchantment = net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.get(id);
            if (enchantment != null) {
                stacks.add(book(enchantment, enchantment.getMaxLevel()));
            }
        });
        return stacks;
    }

    public static ItemStack book(Enchantment enchantment, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, level));
        return stack;
    }
}
