package de.guntram.mcmod.fabrictools.mixins;

import de.guntram.mcmod.fabrictools.KeyBindingManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinProcessKeys {
    @Inject(method="handleInputEvents", at=@At("HEAD"))
    public void onProcessKeybinds(CallbackInfo ci) {
        KeyBindingManager.processKeyBinds();
    }
}
