// Lets the api override any enchantment name without each mod patching Enchantment#getFullname.
// 1.20.1 builds the name as an instance method taking only the level.

package com.huziyang520.merlinlib.mixin;

import com.huziyang520.merlinlib.api.MerlinApi;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinEnchantmentName {
    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true, require = 1)
    private void merlinlib$overrideName(int level, CallbackInfoReturnable<Component> callback) {
        Component name = MerlinApi.enchantmentText().fullName((Enchantment) (Object) this, level);
        if (name != null) {
            callback.setReturnValue(name);
        }
    }
}
