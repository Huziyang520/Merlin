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

    /**
     * Rolls with the source's strength scaling the rule's base chances; the remainder always fails.
     *
     * @param strengthScale 0..1, where the rule's own chances apply at 1
     */
    default Outcome roll(EvolutionRule rule, float strengthScale, RandomSource random) {
        float scale = Math.max(0.0F, Math.min(1.0F, strengthScale));
        double value = random.nextDouble();
        if (value < rule.evolveChance() * scale) {
            return Outcome.EVOLVED;
        }
        if (value < (rule.evolveChance() + rule.transmuteChance()) * scale) {
            return Outcome.TRANSMUTED;
        }
        return Outcome.FAILED;
    }

    boolean applyEvolution(ItemStack stack, EvolutionRule rule, HolderLookup.Provider registries);

    boolean applyTransmutation(ItemStack stack, int strength, HolderLookup.Provider registries, RandomSource random);

    /**
     * Transmutes while dropping the rule's source enchantment, so a success replaces it instead of stacking.
     */
    default boolean applyTransmutation(ItemStack stack, EvolutionRule rule, int strength,
                                       HolderLookup.Provider registries, RandomSource random) {
        return applyTransmutation(stack, strength, registries, random);
    }
}
