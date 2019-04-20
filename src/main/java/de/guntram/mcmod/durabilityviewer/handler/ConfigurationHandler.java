package de.guntram.mcmod.durabilityviewer.handler;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.client.gui.Corner;
import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;
import net.minecraft.text.TextFormat;

public class ConfigurationHandler implements ModConfigurationHandler
{
    private static ConfigurationHandler instance;
    
    private Configuration config;
    private TextFormat tooltipColor;
    private String configFileName;

    private int corner=0;
    private int color= 5;
    private boolean effectDuration;
    private int minPercent = 10;
    private int minDurability = 100;
    private boolean showPlayerServerName;
    private boolean useCustomSound;
    private int showDamageOverPercent;
    
    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }
    
    public void load(final File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(DurabilityViewer.MODNAME)) {
            loadConfig();
        }
    }
    
    private void loadConfig() {
        corner=config.getInt("HUD Corner", Configuration.CATEGORY_CLIENT, corner, 0, 3, "Corner 0 to 3 - bottom right, bottom left, top right, top left");
        color=config.getInt("Tooltip Color", Configuration.CATEGORY_CLIENT, color, 0, 15, "Minecraft Color 0 .. 15");
        effectDuration=config.getBoolean("Effect Duration", Configuration.CATEGORY_CLIENT, true, "Show effect durations");
        minPercent = config.getInt("Minimum Percent", Configuration.CATEGORY_CLIENT, minPercent, 1, 100, "Play sound when durability below X percent");
        minDurability = config.getInt("Minimum Durability", Configuration.CATEGORY_CLIENT, minDurability, 1, 1500, "Play sound when durability below X");
        showPlayerServerName = config.getBoolean("Set window title", Configuration.CATEGORY_CLIENT, true, "Set window title to player and server name");
        showDamageOverPercent = config.getInt("Percent to show damage", Configuration.CATEGORY_CLIENT, 80, 0, 100, "Show damage instead of durability while the item is still better than this %");
        // useCustomSound = config.getBoolean("Use custom sound", Configuration.CATEGORY_CLIENT, false, "Use your own warning sound. You need to create your own custom.ogg in the mod folder");
        
        tooltipColor=TextFormat.byId(color);
        if (config.hasChanged())
            config.save();
    }
    
    public static TextFormat getTooltipColor() {
        return getInstance().tooltipColor;
    }
    
    public static Corner getCorner() {
        return Corner.values()[getInstance().corner];
    }
    
    @Override
    public Configuration getConfig() {
        return getInstance().config;
    }
    
    public static boolean showEffectDuration() {
        return getInstance().effectDuration;
    }
    
    public static int getMinPercent() {
        return getInstance().minPercent;
    }
    
    public static int getMinDurability() {
        return getInstance().minDurability;
    }
    
    public static boolean showPlayerServerName() {
        return getInstance().showPlayerServerName;
    }
    
    public static int showDamageOverPercent() {
        return getInstance().showDamageOverPercent;
    }
    
    public static boolean useCustomSound() {
        // return getInstance().useCustomSound;
        return false;
    }
}
