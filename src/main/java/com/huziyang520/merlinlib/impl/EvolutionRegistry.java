// Default EvolutionApi: rules are registered in code, the transmutation pool is built from the enchantment registry.
// A stack keeps every enchantment it already had; only the new one is added.

package com.huziyang520.merlinlib.impl;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.api.EvolutionApi;
import com.huziyang520.merlinlib.api.EvolutionRule;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

public final class EvolutionRegistry implements EvolutionApi {
    public static final EvolutionRegistry INSTANCE = new EvolutionRegistry();
    private static final int MAX_STRENGTH = 30;

    private final List<EvolutionRule> rules = new ArrayList<>();
    private final Set<ResourceLocation> excluded = new LinkedHashSet<>();

    private EvolutionRegistry() {
    }

    @Override
    public void register(EvolutionRule rule) {
        for (EvolutionRule existing : this.rules) {
            if (existing.from().equals(rule.from())) {
                Constants.LOG.warn("[MerlinLib] evolution rule for {} already exists; {} was ignored",
                        rule.from(), rule.to());
                return;
            }
        }
        this.rules.add(rule);
    }

    @Override
    public List<EvolutionRule> rules() {
        return List.copyOf(this.rules);
    }

    @Override
    public void exclude(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            if (id != null) {
                this.excluded.add(id);
            }
        }
    }

    @Override
    public EvolutionRule match(ItemStack stack) {
        if (stack == null || stack.isEmpty() || this.rules.isEmpty()) {
            return null;
        }
        Map<Enchantment, Integer> carried = EnchantmentHelper.getEnchantments(stack);
        if (carried.isEmpty()) {
            return null;
        }
        for (EvolutionRule rule : this.rules) {
            Enchantment source = BuiltInRegistries.ENCHANTMENT.get(rule.from());
            if (source != null && carried.getOrDefault(source, 0) > 0) {
                return rule;
            }
        }
        return null;
    }

    @Override
    public Outcome roll(EvolutionRule rule, RandomSource random) {
        float value = random.nextFloat();
        if (value < rule.evolveChance()) {
            return Outcome.EVOLVED;
        }
        if (value < rule.evolveChance() + rule.transmuteChance()) {
            return Outcome.TRANSMUTED;
        }
        return Outcome.FAILED;
    }

    @Override
    public boolean applyEvolution(ItemStack stack, EvolutionRule rule) {
        Enchantment target = BuiltInRegistries.ENCHANTMENT.get(rule.to());
        if (target == null) {
            Constants.LOG.warn("[MerlinLib] evolution target {} is not registered; the roll was discarded", rule.to());
            return false;
        }
        int level = Math.max(1, Math.min(target.getMaxLevel(), carriedLevel(stack, rule)));
        return write(stack, levels -> {
            Enchantment source = BuiltInRegistries.ENCHANTMENT.get(rule.from());
            if (source != null) {
                levels.remove(source);
            }
            levels.put(target, level);
        });
    }

    @Override
    public boolean applyTransmutation(ItemStack stack, int strength, RandomSource random) {
        return applyTransmutation(stack, null, strength, random);
    }

    @Override
    public boolean applyTransmutation(ItemStack stack, EvolutionRule rule, int strength, RandomSource random) {
        if (!canStoreEnchantments(stack)) {
            return false;
        }
        List<Enchantment> pool = new ArrayList<>();
        int totalWeight = 0;
        boolean book = stack.is(Items.ENCHANTED_BOOK);
        Map<Enchantment, Integer> carried = EnchantmentHelper.getEnchantments(stack);
        for (Enchantment candidate : ForgeRegistries.ENCHANTMENTS) {
            if (!isEligible(candidate, stack, carried, book)) {
                continue;
            }
            pool.add(candidate);
            totalWeight += Math.max(1, candidate.getRarity().getWeight());
        }
        if (pool.isEmpty() || totalWeight <= 0) {
            return false;
        }
        int pick = random.nextInt(totalWeight);
        Enchantment chosen = pool.get(0);
        for (Enchantment candidate : pool) {
            pick -= Math.max(1, candidate.getRarity().getWeight());
            if (pick < 0) {
                chosen = candidate;
                break;
            }
        }
        int level = levelFor(chosen, strength);
        Enchantment source = rule == null ? null : BuiltInRegistries.ENCHANTMENT.get(rule.from());
        final Enchantment picked = chosen;
        final int pickedLevel = level;
        return write(stack, levels -> {
            if (source != null) {
                levels.remove(source);
            }
            levels.put(picked, pickedLevel);
        });
    }

    private boolean isEligible(Enchantment candidate, ItemStack stack, Map<Enchantment, Integer> carried, boolean book) {
        ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(candidate);
        if (id != null && this.excluded.contains(id)) {
            return false;
        }
        if (carried.containsKey(candidate) || candidate.isCurse()) {
            return false;
        }
        if (!book && !candidate.canEnchant(stack)) {
            return false;
        }
        return EnchantmentHelper.isEnchantmentCompatible(carried.keySet(), candidate);
    }

    private static int levelFor(Enchantment enchantment, int strength) {
        int clamped = Math.max(1, Math.min(MAX_STRENGTH, strength));
        int scaled = Math.round(clamped * (float) enchantment.getMaxLevel() / MAX_STRENGTH);
        return Math.max(1, Math.min(enchantment.getMaxLevel(), scaled));
    }

    private static int carriedLevel(ItemStack stack, EvolutionRule rule) {
        Enchantment source = BuiltInRegistries.ENCHANTMENT.get(rule.from());
        if (source == null) {
            return 1;
        }
        return Math.max(1, EnchantmentHelper.getItemEnchantmentLevel(source, stack));
    }

    private static boolean canStoreEnchantments(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(Items.ENCHANTED_BOOK) || stack.isEnchantable());
    }

    private static boolean write(ItemStack stack, Consumer<Map<Enchantment, Integer>> action) {
        if (!canStoreEnchantments(stack)) {
            return false;
        }
        Map<Enchantment, Integer> levels = new LinkedHashMap<>(EnchantmentHelper.getEnchantments(stack));
        action.accept(levels);
        if (stack.is(Items.ENCHANTED_BOOK)) {
            // The helper appends stored enchantments, so the old list has to go first.
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(EnchantedBookItem.TAG_STORED_ENCHANTMENTS);
            }
        }
        EnchantmentHelper.setEnchantments(levels, stack);
        return true;
    }

    public void clear() {
        this.rules.clear();
        this.excluded.clear();
    }
}
