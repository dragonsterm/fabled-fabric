package fabled.modid.client.render.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fabled.modid.block.custom.WitheredSoulBlock;
import fabled.modid.block.entity.WitheredSoulBlockEntity;
import fabled.modid.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WitheredSoulBlockEntityRenderer implements BlockEntityRenderer<WitheredSoulBlockEntity> {
    private final ItemRenderer itemRenderer;

    public WitheredSoulBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(WitheredSoulBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.85f, 0.5f);

        poseStack.mulPose(Axis.YP.rotationDegrees(-blockEntity.getBlockState().getValue(WitheredSoulBlock.FACING).toYRot()));

        long time = blockEntity.getLevel().getGameTime();
        poseStack.mulPose(Axis.YP.rotationDegrees((time + partialTick) * 2));

        this.itemRenderer.renderStatic(new ItemStack(ModItems.WITHERED_STAR), ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}
