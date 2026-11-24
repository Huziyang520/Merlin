// A generic criterion any dependent mod can fire by condition id.

package com.huziyang520.merlinlib.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class CustomTrigger extends SimpleCriterionTrigger<CustomTriggerInstance> {
    public static final CustomTrigger INSTANCE = new CustomTrigger();

    public CustomTrigger() {
    }

    @Override
    public Codec<CustomTriggerInstance> codec() {
        return CustomTriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Identifier condition) {
        super.trigger(player, instance -> instance.condition().equals(condition));
    }

    public void trigger(ServerPlayer player, Identifier condition, int value) {
        super.trigger(player, instance -> instance.condition().equals(condition)
                && instance.value().map(bounds -> bounds.matches(value)).orElse(true));
    }
}
