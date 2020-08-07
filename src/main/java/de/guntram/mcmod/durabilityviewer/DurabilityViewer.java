package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.GBForgetools.ConfigurationProvider;
import de.guntram.mcmod.GBForgetools.GuiModOptions;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.event.KeyInputEvent;
import de.guntram.mcmod.durabilityviewer.event.TooltipEvent;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import de.guntram.mcmod.durabilityviewer.handler.KeyHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod("durabilityviewer")
public class DurabilityViewer
{
    public static final String MODID = "durabilityviewer";
    public static final String MODNAME = "durabilityviewer";
    public static final String VERSION = "1.6";

    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    private static String changedWindowTitle;

    public DurabilityViewer() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::init);
        // bus.addListener(this::onConnectedToServerEvent);
        // bus.addListener(this::onDisconnectedFromServerEvent);
    }
    
    public void init(final FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            changedWindowTitle=null;
            confHandler=ConfigurationHandler.getInstance();
            confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
            KeyHandler.init();
            System.out.println("on Init, confHandler is "+confHandler);
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(confHandler);
            MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
            MinecraftForge.EVENT_BUS.register(new TooltipEvent());
            MinecraftForge.EVENT_BUS.register(new GuiItemDurability());
        } else {
            System.err.println(MODNAME+" detected a dedicated server. Not doing anything.");
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public static void openConfigScreen() {
        Minecraft.getInstance().displayGuiScreen(new GuiModOptions(null, MODNAME, confHandler));
    }

    /* Seems like this event doesn't exist any more
    public void onConnectedToServerEvent(ClientConnectedToServerEvent event) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getInstance();
        String serverName = (event.isLocal() ? "local game" : mc.getCurrentServerData().serverName);
        if (serverName==null)
            serverName="unknown server";
        changedWindowTitle=mc.getSession().getUsername() + " on "+serverName;
    }

    public void onDisconnectFromServerEvent(ClientDisconnectionFromServerEvent event) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getInstance();
        changedWindowTitle=mc.getSession().getUsername() + " not connected";
    }
    */

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
