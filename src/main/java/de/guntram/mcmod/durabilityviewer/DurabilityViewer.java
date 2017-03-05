package de.guntram.mcmod.durabilityviewer;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.guntram.mcmod.durabilityviewer.handler.KeyHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import de.guntram.mcmod.durabilityviewer.event.KeyInputEvent;
import de.guntram.mcmod.durabilityviewer.event.TooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = DurabilityViewer.MODID, 
        name = "Durability Viewer", 
        version = DurabilityViewer.VERSION,
        clientSideOnly = true, 
        acceptedMinecraftVersions = "[1.11.2]", 
        guiFactory = "de.guntram.mcmod.durabilityviewer.client.gui.DurabilityViewerGuiFactory", 
        dependencies = "")

public class DurabilityViewer
{
    public static final String MODID = "durabilityviewer";
    public static final String VERSION = "1.1.0";

    @Mod.Instance("durabilityviewer")
    public static DurabilityViewer instance;
    private static ConfigurationHandler confHandler;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        confHandler=ConfigurationHandler.getInstance();
        confHandler.load(event.getSuggestedConfigurationFile());
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        KeyHandler.init();
        System.out.println("on Init, confHandler is "+confHandler);
        MinecraftForge.EVENT_BUS.register(confHandler);
        MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        MinecraftForge.EVENT_BUS.register(new GuiItemDurability());
    }
}
