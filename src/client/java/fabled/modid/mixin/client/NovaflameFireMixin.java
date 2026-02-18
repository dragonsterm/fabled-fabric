package fabled.modid.mixin.client;

import fabled.modid.effect.ModEffects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
public abstract class NovaflameFireMixin {

    // Memberikan efek api biru (soul fire) secara otomatis dengan memanipulasi pengecekan blok tanah.
    // method_3976 adalah intermediary name untuk renderFire.
    @Redirect(
        method = {"renderFire", "method_3976"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean fabled$forceBlueFire(BlockState instance, TagKey<Block> tag, PoseStack poseStack, MultiBufferSource buffer, Entity entity, Quaternionf quaternion) {
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.NOVAFLAME)) {
            return true;
        }
        return instance.is(tag);
    }
}
