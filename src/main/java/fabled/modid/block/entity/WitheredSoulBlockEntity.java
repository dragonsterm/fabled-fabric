package fabled.modid.block.entity;

import fabled.modid.block.custom.WitheredSoulBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WitheredSoulBlockEntity extends BlockEntity {
    public WitheredSoulBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WITHERED_SOUL_BLOCK_ENTITY, pos, state);
    }
}
