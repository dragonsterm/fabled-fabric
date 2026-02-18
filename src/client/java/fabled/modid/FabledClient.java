package fabled.modid;

import fabled.modid.block.entity.ModBlockEntities;
import fabled.modid.entity.ModEntities;
import fabled.modid.entity.client.StarSoulRitualRenderer;
import net.fabricmc.api.ClientModInitializer;
import fabled.modid.client.render.block.entity.WitheredSoulBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class FabledClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRenderers.register(ModBlockEntities.WITHERED_SOUL_BLOCK_ENTITY, WitheredSoulBlockEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.STAR_SOUL_RITUAL, StarSoulRitualRenderer::new);
	}
}