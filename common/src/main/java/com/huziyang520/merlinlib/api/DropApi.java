// The one place where a mod puts an item stack into the world at a death or a block.

package com.huziyang520.merlinlib.api;

import com.huziyang520.merlinlib.impl.DropGate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public final class DropApi {
    public static final DropApi INSTANCE = new DropApi();

    private static final ThreadLocal<LivingEntity> CAPTURE_VICTIM = new ThreadLocal<>();
    private final Map<UUID, List<ItemEntity>> capturedDrops = new ConcurrentHashMap<>();
    private final Map<UUID, ScheduledDrop> scheduledDrops = new ConcurrentHashMap<>();

    private DropApi() {
    }

    public void suppressDrops(LivingEntity victim) {
        DropGate.INSTANCE.suppress(victim);
    }

    public boolean dropsSuppressed(LivingEntity victim) {
        return DropGate.INSTANCE.isSuppressed(victim);
    }

    public void releaseDrops(LivingEntity victim) {
        DropGate.INSTANCE.release(victim);
    }

    public boolean dropAtDeath(ServerLevel level, Vec3 pos, ItemStack stack, LivingEntity victim) {
        if (level == null || pos == null || stack == null || stack.isEmpty()) {
            return false;
        }
        if (victim != null && DropGate.INSTANCE.isSuppressed(victim)) {
            return false;
        }
        Block.popResource(level, blockPos(pos), stack);
        return true;
    }

    private static BlockPos blockPos(Vec3 pos) {
        return new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
    }

    public boolean dropAt(ServerLevel level, BlockPos pos, ItemStack stack) {
        if (level == null || pos == null || stack == null || stack.isEmpty()) {
            return false;
        }
        Block.popResource(level, pos, stack);
        return true;
    }

    /** Opens a death-drop capture window for the current thread. */
    public void beginDeathDropCapture(LivingEntity victim) {
        if (victim == null) {
            return;
        }
        CAPTURE_VICTIM.set(victim);
        capturedDrops.computeIfAbsent(victim.getUUID(), key -> new ArrayList<>()).clear();
    }

    public void endDeathDropCapture() {
        CAPTURE_VICTIM.remove();
    }

    /** Captures only items spawned while a capture window is open. */
    public void captureDeathDrop(ItemEntity item) {
        LivingEntity victim = CAPTURE_VICTIM.get();
        if (victim == null || item == null) {
            return;
        }
        capturedDrops.computeIfAbsent(victim.getUUID(), key -> new ArrayList<>()).add(item);
    }

    /** Schedules a captured drop to move to the killer after a delay. */
    public void scheduleDeathDropMove(LivingEntity victim, ServerPlayer killer, ServerLevel level,
                                      int delayTicks, double radius) {
        if (victim == null || killer == null || level == null) {
            return;
        }
        scheduledDrops.put(victim.getUUID(), new ScheduledDrop(level, killer.getUUID(),
                level.getGameTime() + Math.max(1, delayTicks), Math.max(0.0D, radius)));
    }

    public void cancelDeathDropMove(LivingEntity victim) {
        if (victim != null) {
            scheduledDrops.remove(victim.getUUID());
            capturedDrops.remove(victim.getUUID());
        }
    }

    /** Moves captured drops immediately, for callers that do not need a delay. */
    public void moveCapturedDropsTo(LivingEntity victim, ServerPlayer killer, double radius) {
        if (victim == null || killer == null) {
            return;
        }
        move(killer.position(), capturedDrops.remove(victim.getUUID()), Math.max(0.0D, radius));
    }

    /** Advances scheduled captures once per server tick. */
    public void tickDeathDropMoves(ServerLevel level) {
        if (level == null || scheduledDrops.isEmpty()) {
            return;
        }
        long now = level.getGameTime();
        Iterator<Map.Entry<UUID, ScheduledDrop>> iterator = scheduledDrops.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ScheduledDrop> entry = iterator.next();
            ScheduledDrop scheduled = entry.getValue();
            if (scheduled.level() != level || scheduled.fireAt() > now) {
                continue;
            }
            iterator.remove();
            ServerPlayer killer = scheduled.level().getServer().getPlayerList().getPlayer(scheduled.killer());
            move(killer == null ? null : killer.position(),
                    capturedDrops.remove(entry.getKey()), scheduled.radius());
        }
    }

    private static void move(Vec3 feet, List<ItemEntity> items, double radius) {
        if (feet == null || items == null || items.isEmpty()) {
            return;
        }
        for (ItemEntity item : items) {
            if (item == null || !item.isAlive()) {
                continue;
            }
            item.setPos(feet.x + (item.getRandom().nextDouble() - 0.5D) * radius,
                    feet.y, feet.z + (item.getRandom().nextDouble() - 0.5D) * radius);
            item.setDeltaMovement(Vec3.ZERO);
        }
    }

    private record ScheduledDrop(ServerLevel level, UUID killer, long fireAt, double radius) {
    }
}
