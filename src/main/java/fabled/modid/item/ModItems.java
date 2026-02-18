package fabled.modid.item;

import fabled.modid.Fabled;
import fabled.modid.item.custom.EntropysEdgeItem;
import fabled.modid.item.custom.StarSoulItem;
import fabled.modid.item.custom.WitherHandleItem;
import fabled.modid.item.custom.WitheredStarItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    
    public static final Item WITHER_HANDLE = registerItem("wither_handle",
            new WitherHandleItem(new FabricItemSettings().stacksTo(1)));
    
    public static final Item ENTROPYS_EDGE = registerItem("entropys_edge",
            new EntropysEdgeItem(new FabricItemSettings().stacksTo(1)));

    public static final Item STAR_SOUL = registerItem("star_soul",
            new StarSoulItem(new FabricItemSettings().stacksTo(1)));

    public static final Item WITHERED_STAR = registerItem("withered_star",
            new WitheredStarItem(new FabricItemSettings().stacksTo(1)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, 
            new ResourceLocation(Fabled.MOD_ID, name), item);
    }
    
    public static void registerModItems() {
        Fabled.LOGGER.info("Registering Mod Items for " + Fabled.MOD_ID);
    }
}