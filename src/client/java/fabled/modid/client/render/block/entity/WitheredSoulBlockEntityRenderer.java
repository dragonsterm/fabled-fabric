package fabled.modid.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fabled.modid.block.custom.WitheredSoulBlock;
import fabled.modid.block.entity.WitheredSoulBlockEntity;
import fabled.modid.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WitheredSoulBlockEntityRenderer implements BlockEntityRenderer<WitheredSoulBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final ItemStack renderStack = new ItemStack(ModItems.WITHERED_STAR);

    public WitheredSoulBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(WitheredSoulBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        Direction facing = blockEntity.getBlockState().getValue(WitheredSoulBlock.FACING);

        poseStack.translate(facing.getStepX() * 0.5f, facing.getStepY() * 0.5f, facing.getStepZ() * 0.5f);

        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            case UP -> {}
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
        }

        poseStack.scale(0.5f, 0.5f, 0.5f);

        this.itemRenderer.renderStatic(renderStack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}
