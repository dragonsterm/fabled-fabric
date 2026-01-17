package fabled.modid.item.custom;

import fabled.modid.util.StarSoulWither;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class StarSoulItem extends Item {
    public StarSoulItem(Properties properties) {
        super(properties.fireResistant());
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
        if (interactionTarget instanceof WitherSkeleton witherSkeleton) {
            StarSoulWither soulWither = (StarSoulWither) witherSkeleton;

            if (soulWither.fabled$isSummoned() || soulWither.fabled$isConverting()) {
                return InteractionResult.PASS;
            }

            soulWither.fabled$startConversion(100);

            if (!player.level().isClientSide) {
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