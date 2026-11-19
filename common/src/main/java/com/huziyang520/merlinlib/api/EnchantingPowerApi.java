// Enchanting power of a station: shelves around it and enchanted books inside chiseled shelves.
// Strength follows the enchanting table curve, so a station can reach the vanilla maximum.

package com.huziyang520.merlinlib.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface EnchantingPowerApi {
    int MAX_SHELVES = 15;
    int MAX_BOOKS = 30;

    /** Scans with the vanilla caps (15 shelves, 30 books). */
    default Power scan(Level level, BlockPos origin) {
        return scan(level, origin, MAX_SHELVES, MAX_BOOKS);
    }

    Power scan(Level level, BlockPos origin, int maxShelves, int maxBooks);

    /**
     * @param shelves bookshelf blocks that pass the air gap check
     * @param books enchanted books stored in the chiseled shelves among them
     * @param strength min(2 * shelves, books), clamped to 0..30
     */
    record Power(int shelves, int books, int strength) {
        public static final Power EMPTY = new Power(0, 0, 0);
    }
}
