package de.guntram.mcmod.durabilityviewer.handler;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

public class KeyHandler
{
    @SideOnly(Side.CLIENT)
    public static KeyBinding showHud;
    
    public static void init() {
        ClientRegistry.registerKeyBinding(KeyHandler.showHud = new KeyBinding("key.duraview", 35, "key.categories.durabilityviewer"));
    }
}
