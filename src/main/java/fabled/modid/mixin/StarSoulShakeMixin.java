package fabled.modid.mixin;

import fabled.modid.util.StarSoulWither;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public class StarSoulShakeMixin {

    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void fabled$shakingOverlay(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof WitherSkeleton) {
            StarSoulWither wither = (StarSoulWither) this;
            if (wither.fabled$isConverting()) {
                cir.setReturnValue(true);
            }
        }
    }
}