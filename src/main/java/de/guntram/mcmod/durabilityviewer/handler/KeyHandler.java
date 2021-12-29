package de.guntram.mcmod.durabilityviewer.handler;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class KeyHandler
{
    public static KeyMapping showHud;
    
    public static void init() {
        ClientRegistry.registerKeyBinding(KeyHandler.showHud = new KeyMapping("key.durabilityviewer.showhide", 'H', "key.categories.durabilityviewer"));
    }
}
