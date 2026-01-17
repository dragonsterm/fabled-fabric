package fabled.modid.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fabled.modid.entity.custom.StarSoulRitualEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class StarSoulRitualRenderer  extends EntityRenderer<StarSoulRitualEntity> {
    private final ItemRenderer itemRenderer;

    public StarSoulRitualRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }
    @Override
    public void render(StarSoulRitualEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0, 0.5, 0.0);
        poseStack.scale(2.0f, 2.0f, 2.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.tickCount + partialTick * 4f));

        ItemStack stack = new ItemStack(Items.NETHER_STAR);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, 15728880, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(StarSoulRitualEntity entity) {
        return new ResourceLocation("textures/atlas/blocks.png");
    }
}
