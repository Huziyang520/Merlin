// The JSON shape of the generic criterion: a condition id and an optional value bound.

package com.huziyang520.merlinlib.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.Optional;

public record CustomTriggerInstance(Identifier condition, Optional<MinMaxBounds.Ints> value)
        implements SimpleCriterionTrigger.SimpleInstance {
    public static final Codec<CustomTriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("condition").forGetter(CustomTriggerInstance::condition),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("value").forGetter(CustomTriggerInstance::value)
            ).apply(instance, CustomTriggerInstance::new));

    @Override
    public Optional<Holder<LootItemCondition>> player() {
        return Optional.empty();
    }
}
