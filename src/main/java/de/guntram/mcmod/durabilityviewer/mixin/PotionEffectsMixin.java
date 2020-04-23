package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.client.gui.hud.InGameHud;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PotionEffectsMixin {
    
    private static GuiItemDurability gui;
    
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    
    private void afterRenderStatusEffects(CallbackInfo ci) {
        if (gui==null)
            gui=new GuiItemDurability();
        gui.afterRenderStatusEffects(0);
    }

    @Inject(method="render", at=@At(
            value="FIELD", 
            target="Lnet/minecraft/client/options/GameOptions;debugEnabled:Z", 
            opcode = Opcodes.GETFIELD, args = {"log=false"}))
    
    private void beforeRenderDebugScreen(float f, CallbackInfo ci) {
        if (gui==null)
            gui=new GuiItemDurability();
        gui.onRenderGameOverlayPost(0);
    }
}
