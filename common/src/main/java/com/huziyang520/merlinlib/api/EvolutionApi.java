// Enchantment evolution: match a rule against an item, then roll the outcome.
// Transmutation picks an enchantment the item supports, does not already carry and does not conflict with.

package com.huziyang520.merlinlib.api;

import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.RandomSource;

public interface EvolutionApi {
    enum Outcome {
        EVOLVED,

        TRANSMUTED,

        FAILED
    }

    void register(EvolutionRule rule);

    List<EvolutionRule> rules();

    void exclude(Identifier... ids);

    EvolutionRule match(ItemStack stack, HolderLookup.Provider registries);

    Outcome roll(EvolutionRule rule, RandomSource random);

    boolean applyEvolution(ItemStack stack, EvolutionRule rule, HolderLookup.Provider registries);

    boolean applyTransmutation(ItemStack stack, int strength, HolderLookup.Provider registries, RandomSource random);
}
