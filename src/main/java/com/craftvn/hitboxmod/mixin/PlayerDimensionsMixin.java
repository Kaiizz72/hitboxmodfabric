package com.craftvn.hitboxmod.mixin;

import com.craftvn.hitboxmod.HitboxMod;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerDimensionsMixin {

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void biggerHitbox(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (!HitboxMod.ENABLED) return;
        EntityDimensions original = cir.getReturnValue();
        cir.setReturnValue(original.scaled(HitboxMod.SCALE));
    }
}
