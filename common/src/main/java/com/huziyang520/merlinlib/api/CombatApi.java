// The enchantment event api, reached through MerlinApi#combat().

package com.huziyang520.merlinlib.api;

import com.huziyang520.merlinlib.impl.CombatTracker;
import java.lang.reflect.Field;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

public final class CombatApi {
    public static final CombatApi INSTANCE = new CombatApi();

    private static final Field DAMAGE_COOLDOWN = findDamageCooldown();

    private CombatApi() {
    }

    private static Field findDamageCooldown() {
        for (String name : new String[]{"damageCooldownTime", "invulnerableTime"}) {
            try {
                Field field = LivingEntity.class.getField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                // 26.3 renamed the field, older versions still use invulnerableTime.
            }
        }
        return null;
    }

    /** True when the damage came from an arrow fired by a player. */
    public boolean isPlayerArrowShot(DamageSource source) {
        return source != null
                && source.getDirectEntity() instanceof AbstractArrow
                && source.getEntity() instanceof Player;
    }

    /** Horizontal XZ distance, ignoring height so hills do not shorten bow range. */
    public double horizontalDistance(LivingEntity attacker, LivingEntity target) {
        if (attacker == null || target == null) {
            return 0.0D;
        }
        double dx = attacker.getX() - target.getX();
        double dz = attacker.getZ() - target.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /** Clears the target's damage cooldown so the next hit lands immediately. */
    public void clearInvulnerability(LivingEntity entity) {
        if (entity == null || DAMAGE_COOLDOWN == null) {
            return;
        }
        try {
            DAMAGE_COOLDOWN.setInt(entity, 0);
        } catch (IllegalAccessException ignored) {
            // A read-only field only costs a few ticks of damage coalescing.
        }
    }

    public void recordDamage(DamageSnapshot snapshot) {
        CombatTracker.INSTANCE.recordDamage(snapshot);
    }

    public void recordKill(KillInfo kill) {
        CombatTracker.INSTANCE.recordKill(kill);
    }

    public DamageSnapshot lastDamage(LivingEntity victim) {
        return CombatTracker.INSTANCE.lastDamage(victim);
    }

    public KillInfo lastKill(LivingEntity victim) {
        return CombatTracker.INSTANCE.lastKill(victim);
    }

    public void clear(LivingEntity victim) {
        CombatTracker.INSTANCE.clear(victim);
    }

    public void clear() {
        CombatTracker.INSTANCE.clear();
    }

    public void knockback(LivingEntity target, double strength, double dx, double dz) {
        if (target == null || target.level().isClientSide()) {
            return;
        }
        target.knockback(strength, dx, dz, target.damageSources().generic(), 0.0F);
    }
}
