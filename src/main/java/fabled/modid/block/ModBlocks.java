package fabled.modid.block;

import fabled.modid.Fabled;
import fabled.modid.block.custom.WitheredSoulBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;


public class ModBlocks {
    public static final Block WITHERED_SOUL_BLOCK = registerBlock("withered_soul_block",
            new WitheredSoulBlock(FabricBlockSettings.copy(Blocks.SOUL_SAND)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Fabled.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Fabled.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        Fabled.LOGGER.info("Registering Mod Blocks for " + Fabled.MOD_ID);
    }
}
