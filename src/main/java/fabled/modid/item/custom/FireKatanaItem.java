package fabled.modid.item.custom;

import fabled.modid.effect.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireKatanaItem extends SwordItem {
    public FireKatanaItem(Properties properties) {
        super(Tiers.IRON, 2, -2.2f, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.getItem() instanceof IceKatanaItem) {
                return super.hurtEnemy(stack, target, attacker);
            }
        }

        target.setSecondsOnFire(5);

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(itemStack);
        }

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            if (isDualWield(user)) {
                executeDualFireSkill(serverLevel, user);
                applyDualCooldown(user);
                return InteractionResultHolder.sidedSuccess(itemStack, false);
            }

            Vec3 look = user.getLookAngle();

            level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.2f);

            user.setDeltaMovement(look.x * 2.8, 0.2, look.z * 2.8);
            user.hurtMarked = true;

            AABB bundle = user.getBoundingBox().inflate(4.0, 2.0, 4.0);
            List<Entity> entities = level.getEntities(user, bundle);
            boolean hitAnyone = false;

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity target && entity != user) {
                    target.hurt(level.damageSources().playerAttack(user), 6.0f);
                    target.setSecondsOnFire(5);

                    for (int i = 0; i < 20; i++) {
                        double px = target.getX() + (level.random.nextDouble() - 0.5) * 1.5;
                        double py = target.getY() + 1 + (level.random.nextDouble() - 0.5) * 1.5;
                        double pz = target.getZ() + (level.random.nextDouble() - 0.5) * 1.5;
                        serverLevel.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0.1, 0.1, 0.1, 0.2);
                    }
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1, target.getZ(), 5, 0.5, 0.5, 0.5, 0.1);
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY() + 1, target.getZ(), 5, 0.2, 0.2, 0.2, 0.05);

                    level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.6f, 1.5f);
                    hitAnyone = true;
                }
            }

            if (hitAnyone) {
                level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            user.getCooldowns().addCooldown(this, 160);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    private boolean isDualWield(Player user) {
        return user.getOffhandItem().getItem() instanceof IceKatanaItem;
    }

    private void applyDualCooldown(Player user) {
        user.getCooldowns().addCooldown(this, 240);
        user.getCooldowns().addCooldown(user.getOffhandItem().getItem(), 240);
    }

    private void executeDualFireSkill(ServerLevel level, Player user) {
        Vec3 look = user.getLookAngle();
        Vec3 start = user.position();
        Vec3 end = start.add(look.x * 7.0, 0.2, look.z * 7.0);

        user.setDeltaMovement(look.x * 2.2, 0.15, look.z * 2.2);
        user.hurtMarked = true;

        AABB path = new AABB(
            Math.min(start.x, end.x) - 1.5, start.y - 1.0, Math.min(start.z, end.z) - 1.5,
            Math.max(start.x, end.x) + 1.5, start.y + 2.0, Math.max(start.z, end.z) + 1.5
        );

        List<Entity> entities = level.getEntities(user, path);
        boolean hitAny = false;
        LivingEntity bestTarget = null;
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity target && entity != user) {
                Vec3 toTarget = target.position().subtract(start).normalize();
                if (look.dot(toTarget) < 0.3) {
                    continue;
                }

                hitAny = true;
                bestTarget = target;

                target.hurt(level.damageSources().playerAttack(user), 4.0f);
                target.addEffect(new MobEffectInstance(ModEffects.NOVAFLAME, 120, 0));
            }
        }

        if (bestTarget != null) {
            double tx = bestTarget.getX();
            double ty = bestTarget.getY() + 1.0;
            double tz = bestTarget.getZ();

            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = look.cross(up);
            if (right.lengthSqr() < 0.001) {
                right = new Vec3(1, 0, 0);
            } else {
                right = right.normalize();
            }

            for (int i = 0; i < 24; i++) {
                double t = (i / 23.0) * 2.6 - 1.3;
                Vec3 p1 = new Vec3(tx, ty, tz).add(right.scale(t)).add(up.scale(t));
                Vec3 p2 = new Vec3(tx, ty, tz).add(right.scale(t)).add(up.scale(-t));
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, p1.x, p1.y, p1.z, 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, p2.x, p2.y, p2.z, 1, 0, 0, 0, 0);
            }

            for (int i = 0; i < 36; i++) {
                double angle = i * Math.PI * 2 / 36;
                double radius = 1.4 + (i % 4) * 0.2;
                double height = (i % 12) * 0.18;
                double sx = tx + Math.cos(angle) * radius;
                double sz = tz + Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, sx, ty - 0.6 + height, sz, 1, 0, 0, 0, 0);
            }

            for (int i = 0; i < 48; i++) {
                double angle = i * Math.PI * 2 / 48;
                double radius = 1.2 + (i % 6) * 0.25;
                double sx = tx + Math.cos(angle) * radius;
                double sz = tz + Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, sx, ty, sz, 1, 0, 0, 0, 0);
            }

            for (int i = 0; i < 60; i++) {
                double ox = (user.getRandom().nextDouble() - 0.5) * 3.2;
                double oy = (user.getRandom().nextDouble() - 0.5) * 2.4;
                double oz = (user.getRandom().nextDouble() - 0.5) * 3.2;
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx + ox, ty + oy, tz + oz, 1, 0.02, 0.02, 0.02, 0.02);
            }

            level.sendParticles(ParticleTypes.CRIT, tx, ty, tz, 18, 0.6, 0.6, 0.6, 0.12);
            level.playSound(null, tx, ty, tz, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.3f, 1.1f);
            level.playSound(null, tx, ty, tz, SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.9f, 1.4f);
        }

        for (int i = 0; i < 10; i++) {
            double t = (i / 9.0) * 7.0;
            double px = start.x + look.x * t;
            double py = user.getY() + 1.0 + (i % 2 == 0 ? 0.2 : -0.1);
            double pz = start.z + look.z * t;
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, px, py, pz, 1, 0, 0, 0, 0);
        }

        if (!hitAny) {
            double fx = end.x;
            double fy = user.getY() + 1.0;
            double fz = end.z;
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, fx, fy, fz, 3, 0.3, 0.2, 0.3, 0);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, fx, fy, fz, 10, 0.4, 0.2, 0.4, 0.02);
            level.playSound(null, fx, fy, fz, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.fabled.fire_katana.tooltip"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
