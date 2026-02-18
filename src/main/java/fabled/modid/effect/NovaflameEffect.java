package fabled.modid.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class NovaflameEffect extends MobEffect {
    public NovaflameEffect() {
        super(MobEffectCategory.HARMFUL, 0x00000000);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890",
            -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isInstantenous() {
        return false;
    }

    @Override
    public void applyInstantenousEffect(net.minecraft.world.entity.Entity source, net.minecraft.world.entity.Entity indirectSource, LivingEntity target, int amplifier, double health) {
    }

    @SuppressWarnings("resource")
    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        if (!target.level().isClientSide) {
            if (target.getTicksFrozen() < 145) {
                target.setTicksFrozen(145);
            }

            if (target.tickCount % 20 == 0) {
                target.hurt(target.level().damageSources().magic(), 1.0f);
            }

            if (target.level() instanceof ServerLevel serverLevel && target.tickCount % 3 == 0) {
                for (int i = 0; i < 2; i++) {
                    double offsetX = (target.getRandom().nextDouble() - 0.5) * target.getBbWidth() * 1.0;
                    double offsetY = target.getRandom().nextDouble() * target.getBbHeight();
                    double offsetZ = (target.getRandom().nextDouble() - 0.5) * target.getBbWidth() * 1.0;

                    serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ,
                        1,
                        0, 0, 0,
                        0.03
                    );
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
