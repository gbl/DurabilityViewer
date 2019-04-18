/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.fabrictools;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.ServerCommandSource;

/**
 *
 * @author gbl
 */
public class LocalCommandManager {
    
    private static LocalCommandManager instance;
    private CommandDispatcher<ServerCommandSource> dispatcher;
    private CompletableFuture<Suggestions> suggestions;

    private LocalCommandManager() {
    }
    
    public static LocalCommandManager getInstance() {
        if (instance == null) {
            instance=new LocalCommandManager();
            instance.dispatcher=new CommandDispatcher<>();
        }
        return instance;
    }
    
    public static CommandDispatcher<ServerCommandSource> getDispatcher() {
        return getInstance().dispatcher;
    }
    
    public static void dispatchLocalCommand(String s) throws CommandSyntaxException {
        getInstance().dispatcher.execute(s, null);
    }
    
    private Suggestions getSuggestionsFor(String command) {
        if (!command.startsWith("/")) {
		    return null;
        }
        // Don't just pass command; pass a stringreader that skips over the '/',
        // or Suggestions.range won't match the server version.
        StringReader reader=new StringReader(command);
        reader.skip();
        ParseResults<ServerCommandSource> parse = dispatcher.parse(reader, null);
        // We are losing the advantage of using a separate thread here,
        // but the server commands, which imply a network exchange,
        // need it much more than we do.
        suggestions = dispatcher.getCompletionSuggestions(parse);
        Suggestions result = suggestions.join();
        return result;
    }
    
    public static Suggestions getSuggestions(String s) {
        return getInstance().getSuggestionsFor(s);
    }
}
