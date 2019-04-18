/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.fabrictools;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gbl
 */
public class KeyBindingManager {
    
    private static List<KeyBindingHandler> handlers;
    
    public static void register(KeyBindingHandler handler) {
        if (handlers == null)
            handlers = new ArrayList<>();
        if (!handlers.contains(handler))
            handlers.add(handler);
    }
    
    public static void processKeyBinds() {
        for (KeyBindingHandler handler: handlers) {
            handler.processKeyBinds();
        }
    }
}
