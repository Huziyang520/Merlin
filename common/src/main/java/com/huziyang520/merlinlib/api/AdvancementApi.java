// Static facade over the custom trigger and the vanilla advancement stores.

package com.huziyang520.merlinlib.api;

import com.huziyang520.merlinlib.advancement.CustomTrigger;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class AdvancementApi {
    public static final AdvancementApi INSTANCE = new AdvancementApi();
    public static final String DEFAULT_CRITERION = "code";

    private AdvancementApi() {
    }

    public static void trigger(ServerPlayer player, Identifier condition) {
        if (player != null && condition != null) {
            CustomTrigger.INSTANCE.trigger(player, condition);
        }
    }

    public static void trigger(ServerPlayer player, Identifier condition, int value) {
        if (player != null && condition != null) {
            CustomTrigger.INSTANCE.trigger(player, condition, value);
        }
    }

    public static boolean award(ServerPlayer player, Identifier advancementId) {
        return INSTANCE.awardCriterion(player, advancementId, DEFAULT_CRITERION);
    }

    public boolean awardCriterion(ServerPlayer player, Identifier advancementId, String criterion) {
        if (player == null || advancementId == null) {
            return false;
        }
        try {
            AdvancementHolder holder = player.level().getServer().getAdvancements().get(advancementId);
            if (holder == null) {
                return false;
            }
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
            if (progress.isDone()) {
                return false;
            }
            return player.getAdvancements().award(holder, criterion);
        } catch (RuntimeException error) {
            return false;
        }
    }

    public boolean isDone(ServerPlayer player, Identifier advancementId) {
        if (player == null || advancementId == null) {
            return false;
        }
        try {
            AdvancementHolder holder = player.level().getServer().getAdvancements().get(advancementId);
            return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
        } catch (RuntimeException error) {
            return false;
        }
    }

    public boolean holdsAny(ServerPlayer player, TagKey<Enchantment> tag) {
        return this.findAny(player, tag) != null;
    }

    public ItemStack findAny(ServerPlayer player, TagKey<Enchantment> tag) {
        if (player == null || tag == null) {
            return null;
        }
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (this.matches(stack, tag)) {
                return stack;
            }
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (this.matches(stack, tag)) {
                return stack;
            }
        }
        for (int index = 0; index < player.getEnderChestInventory().getContainerSize(); index++) {
            ItemStack stack = player.getEnderChestInventory().getItem(index);
            if (this.matches(stack, tag)) {
                return stack;
            }
        }
        return null;
    }

    private boolean matches(ItemStack stack, TagKey<Enchantment> tag) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        }
        if (enchantments == null || enchantments.isEmpty()) {
            return false;
        }
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.is(tag)) {
                return true;
            }
        }
        return false;
    }
}
