// One rule that turns an enchantment on an item into another enchantment.
// Chances are absolute: evolve first, then transmute, the remainder is a failure.

package com.huziyang520.merlinlib.api;

import net.minecraft.resources.ResourceLocation;

public record EvolutionRule(ResourceLocation from, ResourceLocation to, float evolveChance, float transmuteChance,
                            int lapisCost, int ticks) {
    public EvolutionRule {
        if (from == null || to == null) {
            throw new IllegalArgumentException("[MerlinLib] an evolution rule needs both ids");
        }
        evolveChance = clamp(evolveChance);
        transmuteChance = clamp(transmuteChance);
        if (evolveChance + transmuteChance > 1.0F + 1.0E-4F) {
            throw new IllegalArgumentException("[MerlinLib] evolve + transmute chance exceeds 1 for " + from + " -> " + to);
        }
        lapisCost = Math.max(0, lapisCost);
        ticks = Math.max(20, ticks);
    }

    public float failureChance() {
        return Math.max(0.0F, 1.0F - this.evolveChance - this.transmuteChance);
    }

    private static float clamp(float value) {
        if (Float.isNaN(value)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
