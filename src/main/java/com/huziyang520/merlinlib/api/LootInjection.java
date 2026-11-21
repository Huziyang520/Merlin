// One rule that puts something into loot tables.

package com.huziyang520.merlinlib.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import java.util.List;
import java.util.Set;

public final class LootInjection {
    public enum Form {
        BOOK,

        RANDOM_BOOK,

        ITEM,

        PLAIN_ITEM
    }

    private final Set<String> targetTables;
    private final Form form;
    private final Item item;
    private final Set<String> enchantments;
    private final float chance;
    private final int weight;
    private final int quality;
    private final boolean moddedTables;
    private final List<LootItemFunction.Builder> functions;

    LootInjection(Set<String> targetTables, Form form, Item item, Set<String> enchantments, float chance,
                  int weight, int quality, boolean moddedTables, List<LootItemFunction.Builder> functions) {
        this.targetTables = targetTables;
        this.form = form;
        this.item = item;
        this.enchantments = enchantments;
        this.chance = chance;
        this.weight = weight;
        this.quality = quality;
        this.moddedTables = moddedTables;
        this.functions = List.copyOf(functions);
    }

    public List<LootItemFunction.Builder> getFunctions() {
        return this.functions;
    }

    public boolean allowsModdedTables() {
        return this.moddedTables;
    }

    public Set<String> getTargetTables() {
        return this.targetTables;
    }

    public Form getForm() {
        return this.form;
    }

    public Item getItem() {
        return this.item;
    }

    public Set<String> getEnchantments() {
        return this.enchantments;
    }

    public float getChance() {
        return this.chance;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getQuality() {
        return this.quality;
    }

    @Override
    public String toString() {
        return "LootInjection[" + this.form + " " + this.enchantments + " into " + this.targetTables
                + ", chance " + this.chance + ", weight " + this.weight + ", quality " + this.quality
                + (this.moddedTables ? ", modded tables allowed" : "") + "]";
    }

    public static Item bookItem() {
        return Items.BOOK;
    }
}
