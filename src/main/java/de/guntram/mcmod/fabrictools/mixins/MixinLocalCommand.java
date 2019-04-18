package de.guntram.mcmod.fabrictools.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.guntram.mcmod.fabrictools.LocalCommandManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinLocalCommand {
    @Inject(method="sendChatMessage", at=@At("HEAD"), cancellable=true)
    private void handleLocalCommand(String message, CallbackInfo callbackInfo) {
        if (message.startsWith("/")) {
            try {
                LocalCommandManager.dispatchLocalCommand(message.substring(1));
                callbackInfo.cancel();
            } catch (CommandSyntaxException ex) {
                // Don't do anything, it wasn't intended to be our command.
                // Not cancelling callbackInfo will make MC send the command to the server.
            }
        }
    }
}
