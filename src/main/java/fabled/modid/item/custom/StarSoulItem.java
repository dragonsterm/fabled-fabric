package fabled.modid.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class StarSoulItem extends Item {
    public StarSoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal(""));

        tooltipComponents.add(Component.translatable("tooltip.fabled.star_soul")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(Component.literal(""));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof WitherSkeleton) {
            if (!player.level().isClientSide) {
                ServerLevel level = (ServerLevel) player.level();
                BlockPos pos = interactionTarget.blockPosition();

                level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0f, 1.0f);
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0);

                interactionTarget.discard();

                Random random = new Random();
                int specialIndex = random.nextInt(3);

                for (int i = 0; i < 3; i++) {
                    WitherSkeleton skeleton = EntityType.WITHER_SKELETON.create(level);
                    if (skeleton != null) {
                        skeleton.moveTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);

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

                            skeleton.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.GOLDEN_APPLE));
                            skeleton.setDropChance(EquipmentSlot.OFFHAND, 1.0f);
                        }

                        level.addFreshEntity(skeleton);
                    }
                }

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.YELLOW);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
