// Operator check shared by the commands that change server state.

package com.huziyang520.merlinlib.impl;

import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

public final class PermissionGate {
    private PermissionGate() {
    }

    public static boolean isOperator(Player player) {
        return player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }
}
