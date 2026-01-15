package fabled.modid.block.entity;

import fabled.modid.Fabled;
import fabled.modid.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<WitheredSoulBlockEntity> WITHERED_SOUL_BLOCK_ENTITY;

    public static void registerBlockEntities() {
        WITHERED_SOUL_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Fabled.MOD_ID, "withered_soul_block_entity"), FabricBlockEntityTypeBuilder.create(WitheredSoulBlockEntity::new, ModBlocks.WITHERED_SOUL_BLOCK).build());
        }
}