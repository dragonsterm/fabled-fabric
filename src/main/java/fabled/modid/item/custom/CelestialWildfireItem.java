package fabled.modid.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CelestialWildfireItem extends SwordItem {
    
    private static final int TELEPORT_DISTANCE = 8;
    private static final double EXPLOSION_RADIUS = 5.0;
    private static final float EXPLOSION_DAMAGE = 85.0f;
    
    public CelestialWildfireItem(Properties properties) {
        super(Tiers.NETHERITE, 4, -2.4f, properties.durability(2031));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.fabled.celestial_wildfire.line1")
            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        tooltipComponents.add(Component.translatable("tooltip.fabled.celestial_wildfire.line2")
            .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.fabled.celestial_wildfire.line3")
            .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.fabled.celestial_wildfire.line4")
            .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal(""));
        tooltipComponents.add(Component.translatable("tooltip.fabled.celestial_wildfire.line5")
            .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            Vec3 eyePosition = player.getEyePosition();
            Vec3 lookVector = player.getLookAngle();
            Vec3 targetPos = eyePosition.add(lookVector.scale(TELEPORT_DISTANCE));
            
            BlockHitResult hitResult = level.clip(new ClipContext(
                eyePosition,
                targetPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
            ));
            
            // Calculate teleport position
            Vec3 teleportPos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Vec3 hitPos = hitResult.getLocation();
                double actualDistance = eyePosition.distanceTo(hitPos) - 1.0;
                if (actualDistance < 1.0) actualDistance = 1.0; 
                teleportPos = eyePosition.add(lookVector.scale(actualDistance));
            } else {
    
                teleportPos = targetPos;
            }
            
            // Find safe position 
            Vec3 safePos = findSafePosition(level, teleportPos);
            
            // Teleport the player
            player.teleportTo(safePos.x, safePos.y, safePos.z);
            
            // Reset fall distance
            player.fallDistance = 0.0f;
            
            // Play teleport sound
            level.playSound(null, safePos.x, safePos.y, safePos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            // Spawn particles 
            if (level instanceof ServerLevel serverLevel) {
                spawnTeleportParticles(serverLevel, safePos.x, safePos.y + 1, safePos.z);
            }
            
            // Create explosion
            createExplosion(level, player);
            
            // Apply effect
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1)); // 10 seconds, level 2
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1)); // 20 seconds, level 2
            
            itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            
            return InteractionResultHolder.success(itemStack);
        }
        
        return InteractionResultHolder.pass(itemStack);
    }
    
    private Vec3 findSafePosition(Level level, Vec3 targetPos) {
        Vec3 feetPos = new Vec3(targetPos.x, targetPos.y - 1.62, targetPos.z);
        
        BlockPos feetBlockPos = new BlockPos((int)Math.floor(feetPos.x), (int)Math.floor(feetPos.y), (int)Math.floor(feetPos.z));

        if (isSafePosition(level, feetBlockPos)) {
            return feetPos;
        }
        
        for (int i = 1; i <= 5; i++) {
            BlockPos below = feetBlockPos.below(i);
            if (level.getBlockState(below).isSolid()) {
                BlockPos groundPos = below.above();
                if (isSafePosition(level, groundPos)) {
                    return new Vec3(feetPos.x, groundPos.getY(), feetPos.z);
                }
            }
        }

        for (int i = 1; i <= 5; i++) {
            BlockPos above = feetBlockPos.above(i);
            if (isSafePosition(level, above)) {
                return new Vec3(feetPos.x, above.getY(), feetPos.z);
            }
        }

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                for (int yOffset = -2; yOffset <= 2; yOffset++) {
                    BlockPos testPos = feetBlockPos.offset(xOffset, yOffset, zOffset);
                    if (isSafePosition(level, testPos)) {
                        return new Vec3(testPos.getX() + 0.5, testPos.getY(), testPos.getZ() + 0.5);
                    }
                }
            }
        }
        return feetPos;
    }
    
    private boolean isSafePosition(Level level, BlockPos pos) {
        return !level.getBlockState(pos).isSolid() && 
               !level.getBlockState(pos.above()).isSolid();
    }
    
    private void createExplosion(Level level, Player player) {
        Vec3 center = player.position();
        AABB area = new AABB(center.x - EXPLOSION_RADIUS, center.y - EXPLOSION_RADIUS, center.z - EXPLOSION_RADIUS,
                            center.x + EXPLOSION_RADIUS, center.y + EXPLOSION_RADIUS, center.z + EXPLOSION_RADIUS);
        
        List<Entity> entities = level.getEntities(player, area);
        
        for (Entity entity : entities) {
            // Skip the player who used the sword
            if (entity == player) {
                continue;
            }
            
            if (entity instanceof LivingEntity livingEntity) {
                double distance = entity.distanceTo(player);
                if (distance <= EXPLOSION_RADIUS) {
                    // (closer = more damage)
                    float damage = (float) (EXPLOSION_DAMAGE * (1.0 - (distance / EXPLOSION_RADIUS)));
                    livingEntity.hurt(level.damageSources().explosion(player, player), damage);
                    // Knockback effect
                    Vec3 knockback = entity.position().subtract(center).normalize().scale(1.5);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(knockback.x, 0.5, knockback.z));
                }
            }
        }
        
        // Visual sound effects
        level.playSound(null, center.x, center.y, center.z, 
            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0f, 0.8f);
        
        if (level instanceof ServerLevel serverLevel) {
            spawnExplosionParticles(serverLevel, center.x, center.y, center.z);
        }
    }
    
    private void spawnTeleportParticles(ServerLevel level, double x, double y, double z) {
        for (int i = 0; i < 50; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2;
            double offsetY = level.random.nextDouble() * 2;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2;
            level.sendParticles(ParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ,
                1, 0, 0, 0, 0.1);
        }
    }
    
    private void spawnExplosionParticles(ServerLevel level, double x, double y, double z) {
        for (int i = 0; i < 100; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * EXPLOSION_RADIUS * 2;
            double offsetY = (level.random.nextDouble() - 0.5) * EXPLOSION_RADIUS * 2;
            double offsetZ = (level.random.nextDouble() - 0.5) * EXPLOSION_RADIUS * 2;
            level.sendParticles(ParticleTypes.FLAME, x + offsetX, y + offsetY, z + offsetZ,
                1, 0, 0, 0, 0.1);
        }
    
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1, z, 1, 0, 0, 0, 0);
    }
}