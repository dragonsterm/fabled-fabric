package fabled.modid;

import fabled.modid.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    
    public static final CreativeModeTab FABLED_GROUP = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        new ResourceLocation(Fabled.MOD_ID, "fabled"),
        FabricItemGroup.builder()
            .title(Component.translatable("itemgroup.fabled"))
            .icon(() -> new ItemStack(ModItems.ENTROPYS_EDGE))
            .displayItems((displayContext, entries) -> {
                entries.accept(ModItems.WITHER_HANDLE);
                entries.accept(ModItems.ENTROPYS_EDGE);
            })
            .build()
    );
    
    public static void registerItemGroups() {
        Fabled.LOGGER.info("Registering Item Groups for " + Fabled.MOD_ID);
    }
}