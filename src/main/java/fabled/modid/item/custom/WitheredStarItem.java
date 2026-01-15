package fabled.modid.item.custom;

import fabled.modid.block.ModBlocks;
import fabled.modid.block.custom.WitheredSoulBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WitheredStarItem extends Item {
    public WitheredStarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL)) {
            level.playSound(player, pos, SoundEvents.BRUSH_SAND, SoundSource.BLOCKS, 1.0f, 1.0f);

            for(int i = 0; i < 16; ++i) {
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + (level.random.nextDouble() - 0.5) + 0.5, pos.getY() + 1.05, pos.getZ() + (level.random.nextDouble() - 0.5) + 0.5, 0.0, 0.0, 0.0);
            }
            if (!level.isClientSide()) {
                level.setBlock(pos, ModBlocks.WITHERED_SOUL_BLOCK.defaultBlockState(), 3);
                BlockState newState = ModBlocks.WITHERED_SOUL_BLOCK.defaultBlockState().setValue(WitheredSoulBlock.FACING, context.getClickedFace());
                if (state.is(Blocks.SOUL_SAND)) {
                    newState = newState.setValue(WitheredSoulBlock.SOUL_TYPE, WitheredSoulBlock.SoulType.SAND);
                } else {
                    newState = newState.setValue(WitheredSoulBlock.SOUL_TYPE, WitheredSoulBlock.SoulType.SOIL);
                }
                level.setBlock(pos, newState, 3);
                if (player != null && !player.isCreative()) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal(""));

        tooltipComponents.add(Component.translatable("tooltip.fabled.withered_star")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        tooltipComponents.add(Component.literal(""));
    }

    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
