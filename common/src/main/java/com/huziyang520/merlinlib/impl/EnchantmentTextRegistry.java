// Default EnchantmentTextApi: two override maps, resolved when an enchantment name is built.

package com.huziyang520.merlinlib.impl;

import com.huziyang520.merlinlib.api.EnchantmentTextApi;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantmentTextRegistry implements EnchantmentTextApi {
    public static final EnchantmentTextRegistry INSTANCE = new EnchantmentTextRegistry();

    private final Map<String, Component> replacements = new ConcurrentHashMap<>();
    private final Map<String, UnaryOperator<Component>> styles = new ConcurrentHashMap<>();

    private EnchantmentTextRegistry() {
    }

    @Override
    public String key(Component description) {
        if (description == null) {
            return null;
        }
        return description.getContents() instanceof TranslatableContents contents ? contents.getKey() : null;
    }

    @Override
    public void replace(String langKey, Component text) {
        if (langKey == null) {
            return;
        }
        if (text == null) {
            this.replacements.remove(langKey);
        } else {
            this.replacements.put(langKey, text);
        }
    }

    @Override
    public void style(String langKey, UnaryOperator<Component> style) {
        if (langKey == null) {
            return;
        }
        if (style == null) {
            this.styles.remove(langKey);
        } else {
            this.styles.put(langKey, style);
        }
    }

    @Override
    public void clear(String langKey) {
        if (langKey != null) {
            this.replacements.remove(langKey);
            this.styles.remove(langKey);
        }
    }

    @Override
    public Component fullName(Holder<Enchantment> enchantment, int level) {
        if (enchantment == null) {
            return null;
        }
        String langKey = key(enchantment.value());
        if (langKey == null) {
            return null;
        }
        Component replaced = this.replacements.get(langKey);
        UnaryOperator<Component> style = this.styles.get(langKey);
        if (replaced == null && style == null) {
            return null;
        }
        MutableComponent name = (replaced == null ? enchantment.value().description().copy() : replaced.copy());
        if (style != null) {
            name = style.apply(name).copy();
        }
        if (level != 1) {
            name.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + level));
        }
        return name;
    }
}
