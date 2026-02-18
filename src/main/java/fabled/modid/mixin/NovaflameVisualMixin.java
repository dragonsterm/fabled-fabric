package fabled.modid.mixin;

import fabled.modid.effect.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class NovaflameVisualMixin {

    // Force isOnFire to be true visually for entities with Novaflame effect.
    // This will trigger the fire rendering logic.
    @Inject(method = {"isOnFire", "method_5809"}, at = @At("HEAD"), cancellable = true)
    private void fabled$novaflameFire(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof LivingEntity living && living.hasEffect(ModEffects.NOVAFLAME)) {
            cir.setReturnValue(true);
        }
    }
}
