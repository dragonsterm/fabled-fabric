package fabled.modid.mixin;

import fabled.modid.util.StarSoulWither;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Mob.class)
public class StarSoulMixin implements StarSoulWither {
    @Unique
    private int fabled$conversionTime = -1;
    @Unique
    private static final EntityDataAccessor<Boolean> FABLED_IS_SUMMONED = SynchedEntityData.defineId(WitherSkeleton.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void fabled$defineData(CallbackInfo ci) {
        if ((Object)this instanceof WitherSkeleton) {
            ((Mob)(Object)this).getEntityData().define(FABLED_IS_SUMMONED, false);
        }
    }

    @Override
    public void fabled$startConversion(int time) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        if (this.fabled$isSummoned()) return;

        this.fabled$conversionTime = time;
        Mob self = (Mob)(Object)this;

        if (!self.level().isClientSide) {
            self.level().playSound(null, self.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 1.0f, 1.0f);
        }
        self.setTarget(null);
    }

    @Override
    public boolean fabled$isConverting() {
        return ((Object)this instanceof WitherSkeleton) && this.fabled$conversionTime > 0;
    }

    @Override
    public boolean fabled$isSummoned() {
        return ((Object)this instanceof WitherSkeleton) && ((Mob)(Object)this).getEntityData().get(FABLED_IS_SUMMONED);
    }

    @Override
    public void fabled$setSummoned(boolean summoned) {
        if ((Object)this instanceof WitherSkeleton) {
            ((Mob)(Object)this).getEntityData().set(FABLED_IS_SUMMONED, summoned);
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void fabled$preventTargeting(LivingEntity target, CallbackInfo ci) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        if (target != null && this.fabled$isConverting()) {
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void fabled$tickConversion(CallbackInfo ci) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        Mob self = (Mob)(Object)this;

        if (this.fabled$isConverting()) {
            this.fabled$conversionTime--;

            if (self.getTarget() != null) {
                self.setTarget(null);
            }

            if (self.level().isClientSide && this.fabled$conversionTime % 20 == 0) {
                self.level().addParticle(ParticleTypes.SMOKE, self.getX(), self.getY() + 1.5, self.getZ(), 0.0, 0.1, 0.0);
            }

            if (!self.level().isClientSide) {
                if (this.fabled$conversionTime % 20 == 0) {
                    ((ServerLevel)self.level()).sendParticles(ParticleTypes.SMOKE, self.getX(), self.getY() + 1.5, self.getZ(), 5, 0.2, 0.5, 0.2, 0.01);
                }

                if (this.fabled$conversionTime <= 0) {
                    this.fabled$finishConversion((WitherSkeleton) self);
                }
            }
        }
    }

    @Unique
    private void fabled$finishConversion(WitherSkeleton self) {
        ServerLevel level = (ServerLevel) self.level();

        level.playSound(null, self.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, self.getX(), self.getY(), self.getZ(), 1, 0, 0, 0, 0);

        self.discard();

        Random random = new Random();
        int specialIndex = random.nextInt(3);

        for (int i = 0; i < 3; i++) {
            WitherSkeleton skeleton = EntityType.WITHER_SKELETON.create(level);
            if (skeleton != null) {
                skeleton.moveTo(self.getX(), self.getY(), self.getZ(), 0, 0);

                ((StarSoulWither)skeleton).fabled$setSummoned(true);

                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                skeleton.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                skeleton.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                skeleton.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));

                if (i == specialIndex) {
                    ItemStack sword = skeleton.getItemBySlot(EquipmentSlot.MAINHAND);
                    sword.enchant(Enchantments.SHARPNESS, 5);
                    sword.enchant(Enchantments.FIRE_ASPECT, 2);
                    sword.enchant(Enchantments.UNBREAKING, 3);

                    skeleton.getItemBySlot(EquipmentSlot.HEAD).enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    skeleton.getItemBySlot(EquipmentSlot.CHEST).enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);

                    skeleton.addTag("DropSpecialItem");
                }

                level.addFreshEntity(skeleton);
            }
        }
    }

    @Inject(method = "dropCustomDeathLoot", at = @At("RETURN"))
    private void fabled$dropSpecialLoot(DamageSource source, int looting, boolean recentlyHit, CallbackInfo ci) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        if (((Mob)(Object)this).getTags().contains("DropSpecialItem")) {
            ((Mob)(Object)this).spawnAtLocation(Items.GOLDEN_APPLE);
        }
    }


    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void fabled$saveData(CompoundTag compound, CallbackInfo ci) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        compound.putInt("FabledConversionTime", this.fabled$conversionTime);
        compound.putBoolean("FabledIsSummoned", this.fabled$isSummoned());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void fabled$loadData(CompoundTag compound, CallbackInfo ci) {
        if (!((Object)this instanceof WitherSkeleton)) return;

        if (compound.contains("FabledConversionTime")) {
            this.fabled$conversionTime = compound.getInt("FabledConversionTime");
        }
        if (compound.contains("FabledIsSummoned")) {
            this.fabled$setSummoned(compound.getBoolean("FabledIsSummoned"));
        }
    }
}