// Shared death-save flow: find the protective slot, drop the inventory and resurrect.

package com.huziyang520.merlinlib.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class PlayerDeathApi {
    public static final PlayerDeathApi INSTANCE = new PlayerDeathApi();

    private static final byte TOTEM_ANIMATION_EVENT = 35;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
            EquipmentSlot.FEET, EquipmentSlot.OFFHAND
    };

    private PlayerDeathApi() {
    }

    public EquipmentSlot findArmorWith(ServerPlayer player, String enchantmentId) {
        return findSlotWith(player, Identifier.parse(enchantmentId), ARMOR_SLOTS);
    }

    public EquipmentSlot findArmorWith(ServerPlayer player, Identifier enchantmentId) {
        return findSlotWith(player, enchantmentId, ARMOR_SLOTS);
    }

    public EquipmentSlot findSlotWith(ServerPlayer player, String enchantmentId, EquipmentSlot... slots) {
        return findSlotWith(player, Identifier.parse(enchantmentId), slots);
    }

    public EquipmentSlot findSlotWith(ServerPlayer player, Identifier enchantmentId, EquipmentSlot... slots) {
        if (player == null || enchantmentId == null || slots == null) {
            return null;
        }
        for (EquipmentSlot slot : slots) {
            if (slot != null && levelOf(player.getItemBySlot(slot), enchantmentId) > 0) {
                return slot;
            }
        }
        return null;
    }

    private static int levelOf(ItemStack stack, Identifier enchantmentId) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        for (var entry : stack.getEnchantments().entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            boolean match = holder.unwrapKey()
                    .map(key -> key.identifier().equals(enchantmentId))
                    .orElse(false);
            if (match) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public void dropInventory(ServerPlayer player, EquipmentSlot destroyedSlot) {
        if (player == null) {
            return;
        }
        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (!stack.isEmpty()) {
                drops.add(stack.copy());
                stack.setCount(0);
            }
        }
        for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (slot == destroyedSlot) {
                player.setItemSlot(slot, ItemStack.EMPTY);
                continue;
            }
            drops.add(stack.copy());
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
        for (ItemStack drop : drops) {
            ItemEntity item = player.createItemStackToDrop(drop, true, false);
            if (item != null) {
                player.level().addFreshEntity(item);
            }
        }
    }

    /** Effect lengths are ticks, so callers keep their own seconds-to-ticks helper. */
    public record ResurrectionOptions(
            int darknessTicks,
            int regenerationTicks,
            int regenerationAmplifier,
            int absorptionTicks,
            int absorptionAmplifier,
            int fireResistanceTicks
    ) {
        public ResurrectionOptions {
            darknessTicks = Math.max(0, darknessTicks);
            regenerationTicks = Math.max(0, regenerationTicks);
            regenerationAmplifier = Math.max(0, regenerationAmplifier);
            absorptionTicks = Math.max(0, absorptionTicks);
            absorptionAmplifier = Math.max(0, absorptionAmplifier);
            fireResistanceTicks = Math.max(0, fireResistanceTicks);
        }
    }

    public void resurrect(ServerPlayer player, ResurrectionOptions options) {
        if (player == null || options == null) {
            return;
        }
        player.setHealth(1.0F);
        player.removeEffect(MobEffects.REGENERATION);
        player.removeEffect(MobEffects.ABSORPTION);
        if (options.regenerationTicks() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                    options.regenerationTicks(), options.regenerationAmplifier()));
        }
        if (options.absorptionTicks() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                    options.absorptionTicks(), options.absorptionAmplifier()));
        }
        if (options.fireResistanceTicks() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                    options.fireResistanceTicks(), 0));
        }
        if (options.darknessTicks() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS,
                    options.darknessTicks(), 0));
        }
        if (player.level() instanceof ServerLevel level) {
            level.broadcastEntityEvent(player, TOTEM_ANIMATION_EVENT);
        }
    }
}
