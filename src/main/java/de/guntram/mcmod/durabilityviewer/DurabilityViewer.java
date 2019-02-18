package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.durabilityviewer.mixin.PotionEffectsMixin;
import de.guntram.mcmod.rifttools.ConfigurationProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import org.dimdev.rift.listener.client.KeyBindingAdder;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.dimdev.riftloader.listener.InitializationListener;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class DurabilityViewer implements InitializationListener, KeybindHandler, KeyBindingAdder
{
    public static final String MODID = "durabilityviewer";
    public static final String VERSION = "1.4";

    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;
    private KeyBinding showHide;
    
    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.durabilityviewer.json");
        Mixins.addConfiguration("mixins.riftpatch-de-guntram.json");
        Mixins.addConfiguration("mixins.rifttools-de-guntram.json");
        confHandler=ConfigurationHandler.getInstance();
        ConfigurationProvider.register("Durability Viewer", confHandler);
        confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        changedWindowTitle=null;
    }
    
    public static void setWindowTitle(String s) {
        changedWindowTitle=s;
    }

    @Override
    public void processKeybinds() {
        if (showHide.isPressed()) {
            GuiItemDurability.toggleVisibility();
        }
    }

    @Override
    public Collection<? extends KeyBinding> getKeyBindings() {
        List<KeyBinding> myBindings=new ArrayList();
        
        myBindings.add(showHide = new KeyBinding("key.duraview", GLFW_KEY_H, "key.categories.durabilityviewer"));
        return myBindings;
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
