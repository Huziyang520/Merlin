// Optional mod links: probe mod ids, pick a namespace, resolve items by candidate id.
// Everything here degrades to "absent" so a missing mod never fails registration.

package com.huziyang520.merlinlib.api;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public interface CompatApi {
    boolean loaded(String modId);

    /** True when any of the ids is loaded. */
    default boolean loadedAny(String... modIds) {
        for (String modId : modIds) {
            if (loaded(modId)) {
                return true;
            }
        }
        return false;
    }

    /** First loaded mod id, or null when none of them is present. */
    String firstLoaded(String... modIds);

    /**
     * Resolves an item by the first id that exists in the registry.
     *
     * @param candidateIds full ids such as {@code aether_ii:ambrosium_shard}
     */
    Optional<Holder<Item>> item(String... candidateIds);

    /** Runs a link registration, logging a failure instead of breaking the loading mod. */
    void guarded(String feature, Runnable body);
}
