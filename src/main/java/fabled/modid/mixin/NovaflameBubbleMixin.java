package fabled.modid.mixin;

import fabled.modid.effect.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NovaflameBubbleMixin extends Entity {

    public NovaflameBubbleMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @SuppressWarnings("resource")
    @Inject(method = "tick", at = @At("TAIL"))
    private void fabled$spawnNovaflameParticles(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Spawn partikel HANYA di client side
        if (!self.level().isClientSide) return;
        if (!self.hasEffect(ModEffects.NOVAFLAME)) return;

        // Spawn 4 partikel api biru per tick untuk visibilitas lebih baik
        for (int i = 0; i < 4; i++) {
            double offsetX = (self.getRandom().nextDouble() - 0.5) * self.getBbWidth() * 1.2;
            double offsetY = self.getRandom().nextDouble() * self.getBbHeight();
            double offsetZ = (self.getRandom().nextDouble() - 0.5) * self.getBbWidth() * 1.2;

            self.level().addParticle(
                ParticleTypes.SOUL_FIRE_FLAME,
                self.getX() + offsetX,
                self.getY() + offsetY,
                self.getZ() + offsetZ,
                0, 0.04, 0
            );
        }
    }
}
