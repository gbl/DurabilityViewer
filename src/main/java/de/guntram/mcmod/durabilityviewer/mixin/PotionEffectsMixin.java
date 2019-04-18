package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PotionEffectsMixin {
    
    private static GuiItemDurability gui;
    
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    
    private void onRenderPotionEffects(CallbackInfo ci) {
        if (gui==null)
            gui=new GuiItemDurability();
        gui.onRenderGameOverlayPost(0);
    }
}
