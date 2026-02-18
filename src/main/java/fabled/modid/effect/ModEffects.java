package fabled.modid.effect;

import fabled.modid.Fabled;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
    public static final MobEffect NOVAFLAME = new NovaflameEffect();

    public static void registerEffects() {
        Registry.register(BuiltInRegistries.MOB_EFFECT,
            new ResourceLocation(Fabled.MOD_ID, "novaflame"), NOVAFLAME);
    }
}

