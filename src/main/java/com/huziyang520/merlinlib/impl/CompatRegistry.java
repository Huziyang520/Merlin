// Default CompatApi, backed by the loader platform for mod lookups and the item registry for ids.

package com.huziyang520.merlinlib.impl;

import com.huziyang520.merlinlib.Constants;
import com.huziyang520.merlinlib.api.CompatApi;
import com.huziyang520.merlinlib.platform.Services;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class CompatRegistry implements CompatApi {
    public static final CompatRegistry INSTANCE = new CompatRegistry();

    private CompatRegistry() {
    }

    @Override
    public boolean loaded(String modId) {
        return modId != null && !modId.isEmpty() && Services.PLATFORM.isModLoaded(modId);
    }

    @Override
    public String firstLoaded(String... modIds) {
        for (String modId : modIds) {
            if (loaded(modId)) {
                return modId;
            }
        }
        return null;
    }

    @Override
    public Optional<Holder<Item>> item(String... candidateIds) {
        Registry<Item> items = BuiltInRegistries.ITEM;
        for (String candidate : candidateIds) {
            ResourceLocation id = ResourceLocation.tryParse(candidate);
            if (id == null) {
                continue;
            }
            Optional<? extends Holder<Item>> found = items.getHolder(ResourceKey.create(Registries.ITEM, id));
            if (found.isPresent()) {
                return Optional.of(found.get());
            }
        }
        return Optional.empty();
    }

    @Override
    public void guarded(String feature, Runnable body) {
        try {
            body.run();
        } catch (RuntimeException | LinkageError failure) {
            Constants.LOG.error("[MerlinLib] optional link {} failed to register", feature, failure);
        }
    }
}
