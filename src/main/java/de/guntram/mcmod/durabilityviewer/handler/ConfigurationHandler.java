package de.guntram.mcmod.durabilityviewer.handler;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.client.gui.Corner;
import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;
import net.minecraft.util.Formatting;

public class ConfigurationHandler implements ModConfigurationHandler
{
    private static ConfigurationHandler instance;

    private Configuration config;
    private Formatting tooltipColor;
    private String configFileName;

    private int corner=0;
    private int color= 5;
    private boolean effectDuration;
    private int minPercent = 10;
    private int minDurability = 100;
    private boolean showPlayerServerName;
    private boolean useCustomSound;
    private int showDamageOverPercent;
    private boolean armorAroundHotbar;
    private boolean showChestIcon;
    private boolean showAllTrinkets;

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
    
    @Override
    public void onConfigChanging(ConfigChangedEvent.OnConfigChangingEvent event) {
        if (event.getModID().equals(DurabilityViewer.MODNAME)) {
            switch (event.getItem()) {
                case "HUD Corner": corner=(int)(Integer)(event.getNewValue()); break;
                case "Armor around hotbar": armorAroundHotbar=(boolean)(Boolean)(event.getNewValue()); break;
                case "Show chest icon": showChestIcon=(boolean)(Boolean)(event.getNewValue()); break;
            }
        }
    }
    
    private void loadConfig() {
        corner=config.getInt("HUD Corner", Configuration.CATEGORY_CLIENT, corner, 0, 3, "Corner 0 to 3 - bottom right, bottom left, top right, top left");
        armorAroundHotbar=config.getBoolean("Armor around hotbar", Configuration.CATEGORY_CLIENT, armorAroundHotbar, "Render armor around hotbar (instead of with tools)");
        color=config.getInt("Tooltip Color", Configuration.CATEGORY_CLIENT, color, 0, 15, "Minecraft Color 0 .. 15");
        effectDuration=config.getBoolean("Effect Duration", Configuration.CATEGORY_CLIENT, true, "Show effect durations");
        minPercent = config.getInt("Minimum Percent", Configuration.CATEGORY_CLIENT, minPercent, 1, 100, "Play sound when durability below X percent");
        minDurability = config.getInt("Minimum Durability", Configuration.CATEGORY_CLIENT, minDurability, 1, 1500, "Play sound when durability below X");
        showPlayerServerName = config.getBoolean("Set window title", Configuration.CATEGORY_CLIENT, true, "Set window title to player and server name");
        showDamageOverPercent = config.getInt("Percent to show damage", Configuration.CATEGORY_CLIENT, 80, 0, 100, "Show damage instead of durability while the item is still better than this %");
        // useCustomSound = config.getBoolean("Use custom sound", Configuration.CATEGORY_CLIENT, false, "Use your own warning sound. You need to create your own custom.ogg in the mod folder");
        showChestIcon = config.getBoolean("Show chest icon", Configuration.CATEGORY_CLIENT, true, "Show chest icon with number of free inventory slots");
        showAllTrinkets = config.getBoolean("Show all trinkets", Configuration.CATEGORY_CLIENT, true, "If you have the trinkets mod, show all trinkets even when they don't have durability/damage");

        tooltipColor=Formatting.byColorIndex(color);
        if (config.hasChanged())
            config.save();
    }
    
    public static Formatting getTooltipColor() {
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
    
    public static boolean getArmorAroundHotbar() {
        return getInstance().armorAroundHotbar;
    }

    public static boolean getShowChestIcon() { return getInstance().showChestIcon; }
    
    public static boolean getShowAllTrinkets() { return getInstance().showAllTrinkets; }

    public static boolean useCustomSound() {
        // return getInstance().useCustomSound;
        return false;
    }
}
