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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IceKatanaItem extends SwordItem {
    public IceKatanaItem(Properties properties) {
        super(Tiers.IRON, 2, -2.2f, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            ItemStack offhandStack = player.getOffhandItem();
            if (offhandStack.getItem() instanceof FireKatanaItem) {
                return super.hurtEnemy(stack, target, attacker);
            }
        }

        target.setTicksFrozen(200);

        if (attacker instanceof Player player && !player.level().isClientSide) {
            if (player.level() instanceof ServerLevel serverLevel) {
                scheduleNormalFreezeDamage(serverLevel, player, target);
            }
        }

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
                executeDualIceSkill(serverLevel, user);
                applyDualCooldown(user);
                return InteractionResultHolder.sidedSuccess(itemStack, false);
            }

            level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0f, 0.5f);
            level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 2.5f, 0.8f);
            level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 1.5f, 0.6f);

            AABB area = user.getBoundingBox().inflate(10.0, 3.0, 10.0);
            List<Entity> entities = level.getEntities(user, area);

            for (int ring = 0; ring <= 4; ring++) {
                double radius = ring * 3.0;
                int particleCount = 50 + (ring * 15);
                for (int i = 0; i < particleCount; i++) {
                    double angle = i * Math.PI * 2 / particleCount;
                    double dx = Math.cos(angle) * radius;
                    double dz = Math.sin(angle) * radius;

                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        user.getX() + dx, user.getY() + 0.05, user.getZ() + dz,
                        2, 0.1, 0, 0.1, 0);

                    if (i % 3 == 0) {
                        serverLevel.sendParticles(ParticleTypes.CLOUD,
                            user.getX() + dx, user.getY() + 0.3, user.getZ() + dz,
                            1, 0.05, 0.1, 0.05, 0.01);
                    }
                }
            }

            for (int spike = 0; spike < 40; spike++) {
                double randomAngle = user.getRandom().nextDouble() * Math.PI * 2;
                double randomDist = 2.0 + user.getRandom().nextDouble() * 10.0;
                double dx = Math.cos(randomAngle) * randomDist;
                double dz = Math.sin(randomAngle) * randomDist;

                for (int height = 0; height < 8; height++) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                        user.getX() + dx, user.getY() + (height * 0.3), user.getZ() + dz,
                        1, 0, 0, 0, 0);
                }
            }

            for (int i = 0; i < 80; i++) {
                double angle = i * Math.PI * 2 / 80;
                double radius = 5.0 + user.getRandom().nextDouble() * 4.0;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;
                double dy = user.getRandom().nextDouble() * 3.0;

                serverLevel.sendParticles(ParticleTypes.INSTANT_EFFECT,
                    user.getX() + dx, user.getY() + dy, user.getZ() + dz,
                    1, 0, 0, 0, 0);
            }

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity target && entity != user) {
                    target.hurt(level.damageSources().playerAttack(user), 6.0f);
                    target.setTicksFrozen(600);

                    scheduleFreezeDamage(serverLevel, user, target, 600);

                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                        target.getX(), target.getY() + 1, target.getZ(),
                        30, 0.5, 0.8, 0.5, 0.1);
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                        target.getX(), target.getY() + 1, target.getZ(),
                        15, 0.5, 0.5, 0.5, 0.3);
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                        target.getX(), target.getY() + 0.5, target.getZ(),
                        10, 0.3, 0.5, 0.3, 0.1);
                }
            }

            user.getCooldowns().addCooldown(this, 200);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    private boolean isDualWield(Player user) {
        return user.getOffhandItem().getItem() instanceof FireKatanaItem;
    }

    private void applyDualCooldown(Player user) {
        user.getCooldowns().addCooldown(this, 240);
        user.getCooldowns().addCooldown(user.getOffhandItem().getItem(), 240);
    }

    private void executeDualIceSkill(ServerLevel level, Player user) {
        double radius = 5.0;
        AABB area = user.getBoundingBox().inflate(radius, 2.5, radius);
        List<Entity> entities = level.getEntities(user, area);

        double ux = user.getX();
        double uy = user.getY();
        double uz = user.getZ();

        for (int i = 0; i < 32; i++) {
            double angle = i * Math.PI * 2 / 32;
            double bx = ux + Math.cos(angle) * 2.2;
            double bz = uz + Math.sin(angle) * 2.2;
            level.sendParticles(ParticleTypes.SNOWFLAKE, bx, uy + 0.2, bz, 2, 0.1, 0.1, 0.1, 0.01);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, bx, uy + 0.5, bz, 1, 0, 0, 0, 0);
            }
        }

        for (int ring = 0; ring < 5; ring++) {
            double rRadius = (ring + 1) * 1.0;
            int count = 16 + ring * 8;
            for (int i = 0; i < count; i++) {
                double angle = i * Math.PI * 2 / count;
                double wx = ux + Math.cos(angle) * rRadius;
                double wz = uz + Math.sin(angle) * rRadius;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, wx, uy + 0.1, wz, 1, 0, 0, 0, 0);
                level.sendParticles(ParticleTypes.SNOWFLAKE, wx, uy + 0.3, wz, 3, 0.1, 0.1, 0.1, 0.02);
            }
        }

        level.playSound(null, ux, uy, uz, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.5f, 0.6f);
        level.playSound(null, ux, uy, uz, SoundEvents.PLAYER_HURT_FREEZE, SoundSource.PLAYERS, 1.2f, 0.8f);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity target && entity != user) {
                double tx = target.getX();
                double ty = target.getY();
                double tz = target.getZ();

                target.hurt(level.damageSources().playerAttack(user), 1.5f);

                target.setTicksFrozen(80);
                target.addEffect(new MobEffectInstance(ModEffects.NOVAFLAME, 120, 0));

                for (int pillar = 0; pillar < 8; pillar++) {
                    double pAngle = pillar * Math.PI * 2 / 8;
                    double px = tx + Math.cos(pAngle) * 1.2;
                    double pz = tz + Math.sin(pAngle) * 1.2;

                    for (int h = 0; h < 6; h++) {
                        double ph = ty + h * 0.3;
                        level.sendParticles(ParticleTypes.END_ROD, px, ph, pz, 1, 0, 0, 0, 0);
                        level.sendParticles(ParticleTypes.SNOWFLAKE, px, ph, pz, 2, 0.05, 0.05, 0.05, 0.01);
                    }
                }

                for (int i = 0; i < 20; i++) {
                    double ox = (user.getRandom().nextDouble() - 0.5) * 1.4;
                    double oy = user.getRandom().nextDouble() * 1.5;
                    double oz = (user.getRandom().nextDouble() - 0.5) * 1.4;
                    level.sendParticles(ParticleTypes.SNOWFLAKE, tx + ox, ty + oy, tz + oz, 1, 0.02, 0.02, 0.02, 0.02);
                    if (i % 2 == 0) {
                        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx + ox, ty + oy, tz + oz, 1, 0.01, 0.01, 0.01, 0.01);
                    }
                }

                scheduleDelayedShatter(level, user, target, 30);
            }
        }

        for (int i = 0; i < 40; i++) {
            double angle = user.getRandom().nextDouble() * Math.PI * 2;
            double dist = user.getRandom().nextDouble() * radius;
            double mx = ux + Math.cos(angle) * dist;
            double mz = uz + Math.sin(angle) * dist;
            level.sendParticles(ParticleTypes.CLOUD, mx, uy + 0.1, mz, 1, 0.1, 0.05, 0.1, 0.005);
        }
    }

    private void scheduleDelayedShatter(ServerLevel level, Player user, LivingEntity target, int delayTicks) {
        level.getServer().tell(new net.minecraft.server.TickTask(
            level.getServer().getTickCount() + delayTicks,
            () -> {
                if (target.isAlive() && target.getTicksFrozen() > 0) {
                    double tx = target.getX();
                    double ty = target.getY() + 1.0;
                    double tz = target.getZ();

                    target.hurt(level.damageSources().playerAttack(user), 2.5f);

                    level.sendParticles(ParticleTypes.SNOWFLAKE, tx, ty, tz, 40, 0.6, 0.6, 0.6, 0.15);
                    level.sendParticles(ParticleTypes.CRIT, tx, ty, tz, 20, 0.5, 0.5, 0.5, 0.1);
                    level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx, ty, tz, 15, 0.4, 0.4, 0.4, 0.08);

                    net.minecraft.world.phys.Vec3 knockback = new net.minecraft.world.phys.Vec3(
                        (tx - user.getX()) * 0.3,
                        0.2,
                        (tz - user.getZ()) * 0.3
                    );
                    target.setDeltaMovement(target.getDeltaMovement().add(knockback));

                    level.playSound(null, tx, ty, tz, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.5f, 1.2f);
                    level.playSound(null, tx, ty, tz, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 1.0f, 0.8f);
                }
            }
        ));
    }

    private void scheduleFreezeDamage(ServerLevel level, Player user, LivingEntity target, int freezeTicks) {
        int damageInterval = 10;
        int totalDamageTicks = freezeTicks / damageInterval;

        for (int i = 1; i <= totalDamageTicks; i++) {
            final int tickDelay = i * damageInterval;
            level.getServer().tell(new net.minecraft.server.TickTask(
                level.getServer().getTickCount() + tickDelay,
                () -> {
                    if (target.isAlive() && target.getTicksFrozen() > 0) {
                        target.hurt(level.damageSources().freeze(), 8.0f);
                    }
                }
            ));
        }
    }

    private void scheduleNormalFreezeDamage(ServerLevel level, Player user, LivingEntity target) {
        for (int i = 1; i <= 4; i++) {
            final int tickDelay = i * 20;
            level.getServer().tell(new net.minecraft.server.TickTask(
                level.getServer().getTickCount() + tickDelay,
                () -> {
                    if (target.isAlive() && target.getTicksFrozen() > 0) {
                        target.hurt(level.damageSources().freeze(), 3.0f);
                    }
                }
            ));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.fabled.ice_katana.tooltip"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
