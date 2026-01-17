package fabled.modid.entity;

import fabled.modid.entity.custom.StarSoulRitualEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final EntityType<StarSoulRitualEntity> STAR_SOUL_RITUAL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation("fabled", "star_soul_ritual"),
            FabricEntityTypeBuilder.create(MobCategory.MISC, StarSoulRitualEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static void registerModEntities() {
    }
}
