package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.KeyHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import de.guntram.mcmod.durabilityviewer.event.KeyInputEvent;
import de.guntram.mcmod.durabilityviewer.event.TooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

@Mod(modid = DurabilityViewer.MODID, 
        name = "Durability Viewer", 
        version = DurabilityViewer.VERSION,
        clientSideOnly = true, 
        acceptedMinecraftVersions = "[1.12]", 
        guiFactory = "de.guntram.mcmod.durabilityviewer.client.gui.DurabilityViewerGuiFactory", 
        dependencies = "")

public class DurabilityViewer
{
    public static final String MODID = "durabilityviewer";
    public static final String VERSION = "1.6";

    @Mod.Instance("durabilityviewer")
    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        confHandler=ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        changedWindowTitle=null;
        KeyHandler.init();
        System.out.println("on Init, confHandler is "+confHandler);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(confHandler);
        MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        MinecraftForge.EVENT_BUS.register(new GuiItemDurability());
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getMinecraft();
        String serverName = (event.isLocal() ? "local game" : mc.getCurrentServerData().serverName);
        if (serverName==null)
            serverName="unknown server";
        changedWindowTitle=mc.getSession().getUsername() + " on "+serverName;
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onDisconnectFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getMinecraft();
        changedWindowTitle=mc.getSession().getUsername() + " not connected";
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
