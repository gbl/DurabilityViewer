package de.guntram.mcmod.fabrictools.mixins;

import java.util.Map;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow private static Map<String, Integer> categoryOrderMap;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void insertCategory(String description,
            int keycode, String category, CallbackInfo ci) {
        if (categoryOrderMap.get(category)==null) {
            categoryOrderMap.put(category, categoryOrderMap.size());
        }
    }
}
