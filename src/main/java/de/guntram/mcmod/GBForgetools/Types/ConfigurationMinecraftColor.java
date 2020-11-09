/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GBForgetools.Types;

import java.util.Map;

/**
 *
 * @author gbl
 */
public class ConfigurationMinecraftColor {
    public int colorIndex;
    public String type;         // for gson

    public ConfigurationMinecraftColor(int index) {
        colorIndex = index;
        this.type = this.getClass().getSimpleName();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName()+"[index="+colorIndex+"]";
    }

    public static ConfigurationMinecraftColor fromJsonMap(Map map) {
        ConfigurationMinecraftColor result = new ConfigurationMinecraftColor(0);
        try {
            result.colorIndex = (int)(double)map.get("colorIndex");
        } catch (Exception ex) {
            System.err.println("Exception "+ex+" when reading MinecraftColor from config");
        }
        return result;
    }
}
