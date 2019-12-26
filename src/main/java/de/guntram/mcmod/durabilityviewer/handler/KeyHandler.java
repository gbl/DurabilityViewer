package de.guntram.mcmod.durabilityviewer.handler;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyHandler
{
    public static KeyBinding showHud;
    
    public static void init() {
        ClientRegistry.registerKeyBinding(KeyHandler.showHud = new KeyBinding("key.duraview", 35, "key.categories.durabilityviewer"));
    }
}
