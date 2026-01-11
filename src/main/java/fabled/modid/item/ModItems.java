package fabled.modid.item;

import fabled.modid.Fabled;
import fabled.modid.item.custom.CelestialWildfireItem;
import fabled.modid.item.custom.WitherHandleItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    
    public static final Item WITHER_HANDLE = registerItem("wither_handle", 
        new WitherHandleItem(new FabricItemSettings()));
    
    public static final Item CELESTIAL_WILDFIRE = registerItem("celestial_wildfire",
        new CelestialWildfireItem(new FabricItemSettings().stacksTo(1)));
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, 
            new ResourceLocation(Fabled.MOD_ID, name), item);
    }
    
    public static void registerModItems() {
        Fabled.LOGGER.info("Registering Mod Items for " + Fabled.MOD_ID);
    }
}