// Default EnchantingPowerApi: the enchanting table ring, with an air gap check in front of every shelf.

package com.huziyang520.merlinlib.impl;

import com.huziyang520.merlinlib.api.EnchantingPowerApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

public final class EnchantingPowerScanner implements EnchantingPowerApi {
    public static final EnchantingPowerScanner INSTANCE = new EnchantingPowerScanner();

    private EnchantingPowerScanner() {
    }

    @Override
    public Power scan(Level level, BlockPos origin, int maxShelves, int maxBooks) {
        if (level == null) {
            return Power.EMPTY;
        }
        int shelves = 0;
        int books = 0;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos target = origin.offset(offset);
            var state = level.getBlockState(target);
            boolean chiseled = state.is(Blocks.CHISELED_BOOKSHELF);
            if (!chiseled && !state.is(Blocks.BOOKSHELF)) {
                continue;
            }
            // A shelf only counts when the spot between it and the station is clear.
            if (!level.isEmptyBlock(origin.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2))) {
                continue;
            }
            if (chiseled && level.getBlockEntity(target) instanceof ChiseledBookShelfBlockEntity shelf) {
                int enchanted = 0;
                boolean stored = false;
                for (int slot = 0; slot < ChiseledBookShelfBlockEntity.MAX_BOOKS_IN_STORAGE; slot++) {
                    var book = shelf.getItem(slot);
                    if (book.isEmpty()) {
                        continue;
                    }
                    stored = true;
                    if (book.is(Items.ENCHANTED_BOOK)) {
                        enchanted++;
                    }
                }
                // An empty chiseled shelf is not a shelf; plain books still make it one.
                if (!stored) {
                    continue;
                }
                shelves++;
                books += enchanted;
            } else {
                shelves++;
            }
        }
        shelves = Math.min(shelves, maxShelves);
        books = Math.min(books, maxBooks);
        return new Power(shelves, books, Math.min(2 * shelves, books));
    }
}
