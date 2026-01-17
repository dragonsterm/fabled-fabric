package fabled.modid.entity.custom;

import fabled.modid.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class StarSoulRitualEntity extends Entity {
    private static final EntityDataAccessor<Optional<BlockPos>> ORIGIN = SynchedEntityData.defineId(StarSoulRitualEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Float> START_ANGLE = SynchedEntityData.defineId(StarSoulRitualEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> CRYSTAL_UUID = SynchedEntityData.defineId(StarSoulRitualEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public StarSoulRitualEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    public void setOrigin(BlockPos pos) {
        this.entityData.set(ORIGIN, Optional.of(pos));
    }

    private BlockPos getOrigin() {
        return this.entityData.get(ORIGIN).orElse(this.blockPosition());
    }

    public void setStartAngle(float angle) {
        this.entityData.set(START_ANGLE, angle);
    }

    public float getStartAngle() {
        return this.entityData.get(START_ANGLE);
    }

    public void setCrystalUUID(UUID uuid) {
        this.entityData.set(CRYSTAL_UUID, Optional.of(uuid));
    }

    public UUID getCrystalUUID() {
        return this.entityData.get(CRYSTAL_UUID).orElse(null);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ORIGIN, Optional.empty());
        this.entityData.define(START_ANGLE, 0.0f);
        this.entityData.define(CRYSTAL_UUID, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos origin = getOrigin();
        double centerX = origin.getX() + 0.5;
        double centerY = origin.getY();
        double centerZ = origin.getZ() + 0.5;

        int age = this.tickCount;
        int circleDuration = 60;
        int riseDuration = 20;
        int dropDuration = 10;
        int totalDuration = circleDuration + riseDuration + dropDuration;

        // Check if the crystal is still alive
        if (!this.level().isClientSide) {
            UUID uuid = getCrystalUUID();
            if (uuid != null) {
                Entity crystal = ((ServerLevel) this.level()).getEntity(uuid);
                // If crystal is null (unloaded/gone) or dead, punish the player
                if (crystal == null || !crystal.isAlive()) {
                    this.level().explode(this, centerX, centerY, centerZ, 10.0f, Level.ExplosionInteraction.BLOCK); // Larger explosion
                    this.discard();
                    return;
                }
            }
        }

        float startAngle = getStartAngle();

        if (age < circleDuration) {
            double radius = 2.0;
            double progress = (double) age / circleDuration;
            double angle = startAngle + (progress * Math.PI * 2);

            double yOffset = 1.0 + Math.sin(age * 0.1) * 0.2;

            this.setPos(
                    centerX + Math.cos(angle) * radius,
                    centerY + yOffset,
                    centerZ + Math.sin(angle) * radius
            );
        } else if (age < circleDuration + riseDuration) {
            double progress = (double) (age - circleDuration) / riseDuration;
            double radius = 2.0 * (1 - progress);
            double currentY = (centerY + 1.0) + (2.5 * progress);

            double angle = startAngle + (Math.PI * 2) + (progress * Math.PI);

            this.setPos(
                    centerX + Math.cos(angle) * radius,
                    currentY,
                    centerZ + Math.sin(angle) * radius
            );
        } else if (age < totalDuration) {
            double progress = (double) (age - (circleDuration + riseDuration)) / dropDuration;
            double startY = centerY + 3.5;
            double endY = centerY + 0.5;
            double currentY = startY + (endY - startY) * progress;

            this.setPos(centerX, currentY, centerZ);
        } else {
            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();

                this.level().explode(this, centerX, centerY, centerZ, 6.0f, Level.ExplosionInteraction.BLOCK);
                ItemEntity starSoul = new ItemEntity(this.level(), centerX, centerY, centerZ, new ItemStack(ModItems.STAR_SOUL));

                starSoul.setDefaultPickUpDelay();
                this.level().addFreshEntity(starSoul);

                for (int i = 0; i < 20; i++) {
                    double px = centerX + (this.random.nextDouble() - 0.5) * 2.0;
                    double py = centerY + (this.random.nextDouble() - 0.5) * 2.0;
                    double pz = centerZ + (this.random.nextDouble() - 0.5) * 2.0;
                    serverLevel.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0.1);
                }
                this.discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("OriginX")) {
            this.setOrigin(new BlockPos(compound.getInt("OriginX"), compound.getInt("OriginY"), compound.getInt("OriginZ")));
        }
        if (compound.contains("StartAngle")) {
            this.setStartAngle(compound.getFloat("StartAngle"));
        }
        if (compound.contains("CrystalUUID")) {
            this.setCrystalUUID(compound.getUUID("CrystalUUID"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        BlockPos origin = getOrigin();
        compound.putInt("OriginX", origin.getX());
        compound.putInt("OriginY", origin.getY());
        compound.putInt("OriginZ", origin.getZ());
        compound.putFloat("StartAngle", getStartAngle());
        if (getCrystalUUID() != null) {
            compound.putUUID("CrystalUUID", getCrystalUUID());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}