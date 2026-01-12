package fabled.modid.item.custom;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class EntropysEdgeTier implements Tier {
    @Override
    public int getUses() {
        return 4064;
    }

    @Override
    public float getSpeed() {
        return 9.0f; // Mining speed
    }

    @Override
    public float getAttackDamageBonus() {
        return 11.0f; // Base damage
    }

    @Override
    public int getLevel() {
        return 4; // Mining level
    }

    @Override
    public int getEnchantmentValue() {
        return 15; // Enchantability
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.NETHER_STAR);
    }
}
