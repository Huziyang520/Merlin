// Opens the screens the library owns: the configuration page and the join notice list.

package com.huziyang520.merlinlib.tools.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MerlinScreens {
    private MerlinScreens() {
    }

    public static void openConfigScreen(Screen parent) {
        if (!ConfigScreens.open(parent)) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(Component.translatable("message.merlinlib.config.needs_screen"));
            }
        }
    }

    public static void openNoticesScreen(Screen parent) {
        Minecraft.getInstance().setScreen(new NoticesScreen(parent));
    }
}
