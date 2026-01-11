package fabled.modid.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WitherHandleItem extends Item {
    
    public WitherHandleItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.fabled.wither_handle")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}