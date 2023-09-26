package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PotionEffectsMixin {
    
    private static GuiItemDurability gui;
    
    @Inject(method="renderStatusEffectOverlay", at=@At("RETURN"))
    
    private void afterRenderStatusEffects(DrawContext context, CallbackInfo ci) {
        if (gui==null)
            gui=new GuiItemDurability();
        gui.afterRenderStatusEffects(context, 0);
    }

    @Inject(method="render", at=@At(
            value="INVOKE",
            target="Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z",
            opcode = Opcodes.GETFIELD, args = {"log=false"}))
    
    private void beforeRenderDebugScreen(DrawContext context, float f, CallbackInfo ci) {
        if (gui==null)
            gui=new GuiItemDurability();
        gui.onRenderGameOverlayPost(context, 0);
    }
}
