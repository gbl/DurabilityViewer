package de.guntram.mcmod.durabilityviewer.handler;

import net.minecraft.client.KeyMapping;

public class KeyHandler
{
    public static KeyMapping showHud;
    
    public static void init() {
        showHud = new KeyMapping("key.durabilityviewer.showhide", 'H', "key.categories.durabilityviewer");
    }
}
