package fabled.modid.item.custom;

import fabled.modid.effect.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class DualKatanaHandler {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide || hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            if (!(entity instanceof LivingEntity target)) {
                return InteractionResult.PASS;
            }

            ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);

            boolean isDualWielding = isDualWieldingKatanas(mainHandStack, offHandStack);

            if (!isDualWielding) {
                return InteractionResult.PASS;
            }

            performDualSlashAttack(player, target, offHandStack);

            return InteractionResult.PASS;
        });
    }

    private static boolean isDualWieldingKatanas(ItemStack mainHand, ItemStack offHand) {
        boolean mainIsIce = mainHand.getItem() instanceof IceKatanaItem;
        boolean mainIsFire = mainHand.getItem() instanceof FireKatanaItem;
        boolean offIsIce = offHand.getItem() instanceof IceKatanaItem;
        boolean offIsFire = offHand.getItem() instanceof FireKatanaItem;

        return (mainIsIce && offIsFire) || (mainIsFire && offIsIce);
    }

    private static void performDualSlashAttack(Player player, LivingEntity target, ItemStack offHandWeapon) {
        ServerLevel level = (ServerLevel) player.level();

        float offhandDamage = getWeaponDamage(offHandWeapon);
        target.hurt(level.damageSources().playerAttack(player), offhandDamage);

        MobEffectInstance existingNovaflame = target.getEffect(ModEffects.NOVAFLAME);
        if (existingNovaflame != null) {
            int newDuration = existingNovaflame.getDuration() + 20;
            target.addEffect(new MobEffectInstance(ModEffects.NOVAFLAME, newDuration, 0));
        } else {
            target.addEffect(new MobEffectInstance(ModEffects.NOVAFLAME, 20, 0));
        }

        level.sendParticles(ParticleTypes.SWEEP_ATTACK,
            target.getX() - 0.5, target.getY() + 1, target.getZ(),
            1, 0, 0, 0, 0);

        level.sendParticles(ParticleTypes.SWEEP_ATTACK,
            target.getX() + 0.5, target.getY() + 1, target.getZ(),
            1, 0, 0, 0, 0);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
            SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    private static float getWeaponDamage(ItemStack weapon) {
        if (weapon.getItem() instanceof SwordItem sword) {
            return sword.getDamage();
        }
        return 1.0f;
    }
}
