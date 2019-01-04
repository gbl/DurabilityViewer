package de.guntram.mcmod.durabilityviewer;

import com.mojang.brigadier.CommandDispatcher;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandSource;
import org.dimdev.rift.listener.client.LocalCommandAdder;
import org.dimdev.rift.listener.client.KeyBindingAdder;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.dimdev.riftloader.listener.InitializationListener;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;


public class DurabilityViewer implements InitializationListener, LocalCommandAdder, KeybindHandler, KeyBindingAdder
{
    public static final String MODID = "durabilityviewer";
    public static final String VERSION = "1.5";

    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;
    private static boolean wantGuiOpen;
    private KeyBinding showHide;

    @Override
    public void onInitialization() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.durabilityviewer.json");
        Mixins.addConfiguration("mixins.riftpatch-de-guntram.json");
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        confHandler=ConfigurationHandler.getInstance();
        ConfigManager.getInstance().registerConfigHandler(MODID, confHandler);
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
    
    // We can't just open the config GUI from the command handler,
    // because the chat GUI is still open, and MC will close the
    // (chat) GUI immediately after the command handler, thereby
    // closing the config GUI. So we just have a flag here and set
    // it from GuiItemDurability on the next render. Doh!

    public static void queueConfigGuiOpen() {
        wantGuiOpen=true;
    }
    
    public static boolean ConfigGuiOpenQueued() {
        boolean temp=wantGuiOpen;
        wantGuiOpen=false;
        return temp;
    }

    public static void setWindowTitle(String s) {
        changedWindowTitle=s;
    }

    @Override
    public void registerLocalCommands(CommandDispatcher<CommandSource> cd) {
        ConfigCommand.register(cd);
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

    private static class InitHandler implements IInitializationHandler {
        @Override
        public void registerModHandlers() {
            IRenderer renderer = new GuiItemDurability();
            RenderEventHandler.getInstance().registerGameOverlayRenderer(renderer);
        }
    }
}
