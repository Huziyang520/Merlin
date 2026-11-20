// Default EvolutionApi: rules are registered in code, the transmutation pool is built from the enchantment registry.
// A stack keeps every enchantment it already had; only the new one is added.

package com.huziyang520.merlinlib.impl;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.api.EvolutionApi;
import com.huziyang520.merlinlib.api.EvolutionRule;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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
    public EvolutionRule match(ItemStack stack, HolderLookup.Provider registries) {
        if (stack == null || stack.isEmpty() || this.rules.isEmpty()) {
            return null;
        }
        ItemEnchantments carried = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        if (carried.isEmpty()) {
            return null;
        }
        HolderLookup.RegistryLookup<Enchantment> lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        for (EvolutionRule rule : this.rules) {
            Optional<Holder.Reference<Enchantment>> source = lookup.get(key(rule.from()));
            if (source.isPresent() && carried.getLevel(source.get()) > 0) {
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
    public boolean applyEvolution(ItemStack stack, EvolutionRule rule, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> target = lookup.get(key(rule.to()));
        if (target.isEmpty()) {
            Constants.LOG.warn("[MerlinLib] evolution target {} is not registered; the roll was discarded", rule.to());
            return false;
        }
        int level = Math.max(1, Math.min(target.get().value().getMaxLevel(), carriedLevel(stack, rule, lookup)));
        return write(stack, mutable -> {
            Optional<Holder.Reference<Enchantment>> source = lookup.get(key(rule.from()));
            source.ifPresent(holder -> mutable.set(holder, 0));
            mutable.set(target.get(), level);
        });
    }

    @Override
    public boolean applyTransmutation(ItemStack stack, int strength, HolderLookup.Provider registries,
                                      RandomSource random) {
        return applyTransmutation(stack, null, strength, registries, random);
    }

    @Override
    public boolean applyTransmutation(ItemStack stack, EvolutionRule rule, int strength,
                                      HolderLookup.Provider registries, RandomSource random) {
        if (!EnchantmentHelper.canStoreEnchantments(stack)) {
            return false;
        }
        HolderLookup.RegistryLookup<Enchantment> lookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder.Reference<Enchantment>> pool = new ArrayList<>();
        int totalWeight = 0;
        boolean book = stack.is(Items.ENCHANTED_BOOK);
        ItemEnchantments carried = stack.getEnchantments();
        Set<Holder<Enchantment>> owned = new HashSet<>(carried.keySet());
        for (Holder.Reference<Enchantment> candidate : lookup.listElements().toList()) {
            if (!isEligible(candidate, stack, owned, book)) {
                continue;
            }
            pool.add(candidate);
            totalWeight += Math.max(1, candidate.value().getWeight());
        }
        if (pool.isEmpty() || totalWeight <= 0) {
            return false;
        }
        int pick = random.nextInt(totalWeight);
        Holder.Reference<Enchantment> chosen = pool.getFirst();
        for (Holder.Reference<Enchantment> candidate : pool) {
            pick -= Math.max(1, candidate.value().getWeight());
            if (pick < 0) {
                chosen = candidate;
                break;
            }
        }
        int level = levelFor(chosen.value(), strength);
        Optional<Holder.Reference<Enchantment>> source =
                rule == null ? Optional.empty() : lookup.get(key(rule.from()));
        final Holder.Reference<Enchantment> picked = chosen;
        final int pickedLevel = level;
        return write(stack, mutable -> {
            source.ifPresent(holder -> mutable.set(holder, 0));
            mutable.set(picked, pickedLevel);
        });
    }

    private boolean isEligible(Holder.Reference<Enchantment> candidate, ItemStack stack, Set<Holder<Enchantment>> owned,
                               boolean book) {
        Optional<ResourceKey<Enchantment>> key = candidate.unwrapKey();
        if (key.isPresent() && this.excluded.contains(key.get().location())) {
            return false;
        }
        if (owned.contains(candidate) || candidate.is(net.minecraft.tags.EnchantmentTags.CURSE)) {
            return false;
        }
        if (!book && !candidate.value().canEnchant(stack)) {
            return false;
        }
        return EnchantmentHelper.isEnchantmentCompatible(owned, candidate);
    }

    private static int levelFor(Enchantment enchantment, int strength) {
        int clamped = Math.max(1, Math.min(MAX_STRENGTH, strength));
        int scaled = Math.round(clamped * (float) enchantment.getMaxLevel() / MAX_STRENGTH);
        return Math.max(1, Math.min(enchantment.getMaxLevel(), scaled));
    }

    private static int carriedLevel(ItemStack stack, EvolutionRule rule, HolderLookup.RegistryLookup<Enchantment> lookup) {
        Optional<Holder.Reference<Enchantment>> source = lookup.get(key(rule.from()));
        ItemEnchantments carried = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        return source.map(enchantment -> Math.max(1, carried.getLevel(enchantment))).orElse(1);
    }

    private static boolean write(ItemStack stack, java.util.function.Consumer<ItemEnchantments.Mutable> action) {
        if (!EnchantmentHelper.canStoreEnchantments(stack)) {
            return false;
        }
        ItemEnchantments.Mutable mutable =
                new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        action.accept(mutable);
        ItemEnchantments result = mutable.toImmutable();
        if (stack.is(Items.ENCHANTED_BOOK)) {
            stack.set(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS, result);
        } else {
            stack.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, result);
        }
        return true;
    }

    private static ResourceKey<Enchantment> key(ResourceLocation id) {
        return ResourceKey.create(Registries.ENCHANTMENT, id);
    }

    public void clear() {
        this.rules.clear();
        this.excluded.clear();
    }
}
