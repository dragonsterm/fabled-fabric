package fabled.modid;

import fabled.modid.block.entity.ModBlockEntities;
import fabled.modid.entity.ModEntities;
import fabled.modid.entity.client.StarSoulRitualRenderer;
import fabled.modid.effect.ModEffects;
import fabled.modid.client.render.block.entity.WitheredSoulBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;

public class FabledClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRenderers.register(ModBlockEntities.WITHERED_SOUL_BLOCK_ENTITY, WitheredSoulBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.STAR_SOUL_RITUAL, StarSoulRitualRenderer::new);

		HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
			Minecraft client = Minecraft.getInstance();
			Player player = client.player;

			if (player != null && player.hasEffect(ModEffects.NOVAFLAME)) {
				int width = client.getWindow().getGuiScaledWidth();
				int height = client.getWindow().getGuiScaledHeight();
				// Semi-transparent Cyan (0x4400FBFF)
				guiGraphics.fill(0, 0, width, height, 0x4400FBFF);
			}
		});
	}
}