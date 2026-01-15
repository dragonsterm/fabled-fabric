package fabled.modid.mixin;

import fabled.modid.block.ModBlocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkullBlock.class)
public class WitheredStarSummonMixin {
    @Nullable
    private static BlockPattern fragmentedSingularityPattern;

    private static BlockPattern getFragmentedSingularityPattern() {
        if (fragmentedSingularityPattern == null) {
            fragmentedSingularityPattern = BlockPatternBuilder.start()
                    .aisle("^^^", "#@#", "~#~")
                    .where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL))))
                    .where('@', BlockInWorld.hasState(BlockStatePredicate.forBlock(ModBlocks.WITHERED_SOUL_BLOCK)))
                    .where('#', BlockInWorld.hasState(state -> state.is(BlockTags.WITHER_SUMMON_BASE_BLOCKS)))
                    .where('~', BlockInWorld.hasState(state -> true))
                    .build();
        }
        return fragmentedSingularityPattern;
    }

    @Inject(method = "checkSpawn", at = @At("HEAD"), cancellable = true)
    private static void checkFragmentedSingularitySpawn(Level level, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo callbackInfo) {
        if (!level.isClientSide) {
            BlockPattern.BlockPatternMatch match = getFragmentedSingularityPattern().find(level, pos);
            if (match != null) {
                for (int i = 0; i < match.getWidth(); ++i) {
                    for (int j = 0; j < match.getWidth(); ++j) {
                        BlockInWorld blockInWorld = match.getBlock(i, j, 0);
                        level.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                        level.levelEvent(2001, blockInWorld.getPos(), net.minecraft.world.level.block.Block.getId(blockInWorld.getState()));

                    }
                }

                WitherBoss witherBoss = EntityType.WITHER.create(level);
                if (witherBoss != null) {
                    BlockPos blockPos = match.getBlock(1, 2, 0).getPos();
                    witherBoss.moveTo((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.55D, (double)blockPos.getZ() + 0.5D, match.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                    witherBoss.yBodyRot = match.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                    witherBoss.makeInvulnerable();

                    witherBoss.setCustomName(Component.literal("Fragmented Singularity"));
                    witherBoss.addTag("FragmentedSingularity");

                    for(ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, witherBoss.getBoundingBox().inflate(50.0D))) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, witherBoss);
                    }

                    level.addFreshEntity(witherBoss);

                    callbackInfo.cancel();
                }
            }
        }
    }
}
