// Text of vanilla and modded enchantments, keyed by the lang key of their description.
// A key survives reloads and works for any enchantment, so no registry access is needed.

package com.huziyang520.merlinlib.api;

import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

public interface EnchantmentTextApi {
    /** Lang key behind a description component, or null when it is not translatable. */
    String key(Component description);

    default String key(Enchantment enchantment) {
        return enchantment == null ? null : key(enchantment.description());
    }

    default boolean matches(Enchantment enchantment, String langKey) {
        return langKey != null && langKey.equals(key(enchantment));
    }

    /** Replaces the whole name; the level suffix is still appended by the caller. */
    void replace(String langKey, Component text);

    /** Restyles the name after any replacement, e.g. a rainbow gradient. */
    void style(String langKey, UnaryOperator<Component> style);

    void clear(String langKey);

    /** Name with overrides applied, or null when this enchantment is untouched. */
    Component fullName(Holder<Enchantment> enchantment, int level);
}
