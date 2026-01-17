package fabled.modid.mixin;

import fabled.modid.entity.ModEntities;
import fabled.modid.entity.custom.StarSoulRitualEntity;
import fabled.modid.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndCrystal.class)
public abstract class StarSoulEndCrystalMixin extends Entity {
    public StarSoulEndCrystalMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.NETHER_STAR) || stack.is(ModItems.STAR_SOUL)) {
            Level level = this.level();
            if (!level.isClientSide) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                StarSoulRitualEntity ritual = new StarSoulRitualEntity(ModEntities.STAR_SOUL_RITUAL, level);
                ritual.setPos(this.getX(), this.getY(), this.getZ());
                ritual.setOrigin(this.blockPosition());

                double angle = Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX());
                ritual.setStartAngle((float) angle);
                
                level.addFreshEntity(ritual);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.interact(player, hand);
    }
}