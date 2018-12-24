package de.guntram.mcmod.durabilityviewer.handler;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.client.gui.Corner;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import java.io.File;
import net.minecraft.util.text.TextFormatting;

public class ConfigurationHandler implements IConfigHandler
{
    public final static String CONFIG_FILE_NAME=DurabilityViewer.MODID+".json";

    private static ConfigurationHandler instance;
    
    private TextFormatting tooltipColor;
    
    public static class Options {
        public static final ConfigInteger       CORNER = new ConfigInteger("Hud Corner", 0, 0, 3, "Corner 0 to 3 - bottom right, bottom left, top right, top left");
        public static final ConfigInteger       COLOR  = new ConfigInteger("Tooltip Color", 5, 0, 15, "Minecraft Color 0 .. 15");
        public static final ConfigBoolean       SHOWEFF= new ConfigBoolean("Effect Duration", true, "Show effect durations");
        public static final ConfigInteger       MINPERC= new ConfigInteger("Minimum Percent", 10, 1, 100, "Play sound when durability below X percent");
        public static final ConfigInteger       MINDUR = new ConfigInteger("Minimum Durability", 100, 1, 1500, "Play sound when durability below X");
        public static final ConfigBoolean       SHOWPS = new ConfigBoolean("Set window title", true, "Set window title to player and server name");

        public static final ImmutableList<IConfigBase> OPTIONS=ImmutableList.of(
                CORNER, COLOR, SHOWEFF, MINPERC, MINDUR, SHOWPS
        );
    }
    
    public static class Hotkeys {
        public static final ConfigHotkey        HOTKEY = new ConfigHotkey("Toggle Display", "H", KeybindSettings.RELEASE_EXCLUSIVE, "Toggles visibility of the Durability Display");
        public static final ConfigHotkey        CONFIG = new ConfigHotkey("Open Config", "H,C", "Shows the config screen");
        public static final ImmutableList<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
                HOTKEY, CONFIG
        );
    }
    
    public static ConfigurationHandler getInstance() {
        if (instance==null) {
            instance=new ConfigurationHandler();
        }
        return instance;
    }
    
    public static TextFormatting getTooltipColor() {
        return getInstance().tooltipColor == null ? TextFormatting.GOLD : getInstance().tooltipColor;
    }
    
    public static Corner getCorner() {
        return Corner.values()[Options.CORNER.getIntegerValue()];
    }
    
    public static boolean showEffectDuration() {
        return Options.SHOWEFF.getBooleanValue();
    }
    
    public static int getMinPercent() {
        return Options.MINPERC.getIntegerValue();
    }
    
    public static int getMinDurability() {
        return Options.MINDUR.getIntegerValue();
    }
    
    public static boolean showPlayerServerName() {
        return Options.SHOWPS.getBooleanValue();
    }
    
    public static IKeybind getHotkey() {
        return Hotkeys.HOTKEY.getKeybind();
    }
    
    public static IKeybind getConfigkey() {
        return Hotkeys.CONFIG.getKeybind();
    }
    
    @Override
    public void onConfigsChanged() {
        save();
        load();
    }

    @Override
    public void load() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Options", Options.OPTIONS);
                ConfigUtils.readHotkeys(root, "Toggles", Hotkeys.HOTKEY_LIST);
            }    
        }
        tooltipColor=TextFormatting.fromColorIndex(Options.COLOR.getIntegerValue());
    }

    @Override
    public void save() {
        File configDir=FileUtils.getConfigDirectory();
        if (!(configDir.exists()))
            configDir.mkdirs();
        JsonObject root=new JsonObject();
        ConfigUtils.writeConfigBase(root, "Options", Options.OPTIONS);
        ConfigUtils.writeHotkeys(root, "Toggles", Hotkeys.HOTKEY_LIST);
        JsonUtils.writeJsonToFile(root, new File(configDir, CONFIG_FILE_NAME));
    }
}
