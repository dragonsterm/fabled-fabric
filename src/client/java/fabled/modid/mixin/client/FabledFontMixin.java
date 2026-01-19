package fabled.modid.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public class FabledFontMixin {
    @Unique
    private static final ResourceLocation ICON_FONT_ID = new ResourceLocation("fabled", "rightclickicons");

    @ModifyVariable(
            method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private boolean fabled$removeShadowForIcons(boolean dropShadow, FormattedCharSequence text) {
        if (dropShadow) {
            boolean[] hasIcon = new boolean[1];
            text.accept((index, style, codePoint) -> {
                if (ICON_FONT_ID.equals(style.getFont())) {
                    hasIcon[0] = true;
                    return false;
                }
                return true;
            });
            if (hasIcon[0]) return false;
        }
        return dropShadow;
    }
}
