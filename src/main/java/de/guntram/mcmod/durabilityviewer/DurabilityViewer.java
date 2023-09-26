package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.crowdintranslate.CrowdinTranslate;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class DurabilityViewer implements ClientModInitializer
{
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";

    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;
    private KeyBinding showHide;
    
    @Override
    public void onInitializeClient() {
        CrowdinTranslate.downloadTranslations(MODID);
        setKeyBindings();
        confHandler=ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        changedWindowTitle=null;
        registerEvents();
    }
    
    public static void setWindowTitle(String s) {
        changedWindowTitle=s;
    }
    
    public static String getWindowTitle() {
        return changedWindowTitle;
    }

    public void processKeyBinds() {
        if (showHide.wasPressed()) {
            GuiItemDurability.toggleVisibility();
        }
    }

    public void setKeyBindings() {
        final String category="key.categories.durabilityviewer";
        KeyBindingHelper.registerKeyBinding(showHide = new KeyBinding("key.durabilityviewer.showhide", InputUtil.Type.KEYSYM, GLFW_KEY_H, category));
        ClientTickEvents.END_CLIENT_TICK.register(e->processKeyBinds());
    }

    private void registerEvents() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!ConfigurationHandler.showPlayerServerName())
                return;
            MinecraftClient mc=MinecraftClient.getInstance();
            ServerInfo serverData = mc.getCurrentServerEntry();
            String serverName;
            if (serverData==null)
                serverName="local game";
            else
                serverName = serverData.name;
            if (serverName==null)
                serverName="unknown server";
            DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " on "+serverName);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!ConfigurationHandler.showPlayerServerName())
                return;
            MinecraftClient mc=MinecraftClient.getInstance();
            DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " not connected");
        });

    }
}
