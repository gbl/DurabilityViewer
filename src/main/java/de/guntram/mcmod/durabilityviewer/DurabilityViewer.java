package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;

public class DurabilityViewer implements ClientModInitializer
{
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "Durability Viewer";
    public static final String VERSION = "1.15-fabric0.4.23-1.7";

    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;
    private FabricKeyBinding showHide;
    
    @Override
    public void onInitializeClient() {
        setKeyBindings();
        confHandler=ConfigurationHandler.getInstance();
        ConfigurationProvider.register(MODNAME, confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        changedWindowTitle=null;
    }
    
    public static void setWindowTitle(String s) {
        changedWindowTitle=s;
    }

    public void processKeyBinds() {
        if (showHide.wasPressed()) {
            GuiItemDurability.toggleVisibility();
        }
    }

    public void setKeyBindings() {
        final String category="key.categories.durabilityviewer";
        KeyBindingRegistry.INSTANCE.addCategory(category);
        KeyBindingRegistry.INSTANCE.register(
            showHide = FabricKeyBinding.Builder
            .create(new Identifier("durabilityviewer:showhide"), InputUtil.Type.KEYSYM, GLFW_KEY_H, category)
            .build());
        ClientTickCallback.EVENT.register(e->processKeyBinds());
    }    

    // On windows, Display.setTitle crashes if we call it from 
    // (Dis)connectFromServerEvent. This is because these run on the netty
    // thread, set a global lock, send a WM_SETTEXT, and need the main thread
    // to process that WM_SETTEXT - but the main thread needs the global lock
    // as well. As a workaround, we just set a global variable here, and retrieve
    // it from within onRender from GuiItemDurability.
    public static String getAndResetChangedWindowTitle() {
        String result=changedWindowTitle;
        changedWindowTitle=null;
        return result;
    }
}
