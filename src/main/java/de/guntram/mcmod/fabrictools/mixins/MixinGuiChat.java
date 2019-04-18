package de.guntram.mcmod.fabrictools.mixins;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.ingame.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mojang.brigadier.suggestion.Suggestions;
import de.guntram.mcmod.fabrictools.LocalCommandManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatScreen.class)
public class MixinGuiChat {
    
    @Shadow TextFieldWidget chatField;
    
    @Redirect(method="openSuggestionsWindow",     // showSuggestions
            at=@At(value="INVOKE",
                   target="Ljava/util/concurrent/CompletableFuture;join()Ljava/lang/Object;",
                   remap=false)
            )

    // We need to declare this to return Object, not Suggestions, because the
    // redirected method is type polymorphic and thus returns Object,
    // not Suggestions, in its byte code definition.

    public Object gotServerSideSuggestions(CompletableFuture<Suggestions> pendingSuggestions) {
        Suggestions server=pendingSuggestions.join();
        Suggestions local=LocalCommandManager.getSuggestions(chatField.getText());

        if (local==null || local.isEmpty()) {
            return server;
        }
        
        if (server.isEmpty()) {
            return local;
        }
        
        if (!local.getRange().equals(server.getRange())) {
            System.err.println("something wrong with ranges");
            return server;
        }

        List<Suggestion> results=new ArrayList<>();
        results.addAll(server.getList());
        results.addAll(local.getList());
        Suggestions result=new Suggestions(server.getRange(), results);
        return result;
    }
}
