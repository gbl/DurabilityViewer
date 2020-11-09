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
public class ConfigurationTrueColor {
    public int red, green, blue;
    public String type;             // for gson
    
    public ConfigurationTrueColor(int rgb) {
        red = (rgb >> 16 ) & 0xff;
        green = (rgb >> 8) & 0xff;
        blue = rgb & 0xff;
        this.type = this.getClass().getSimpleName();
    }
    
    public int getInt() {
        return red << 16 | green << 8 | blue;
    }

    public static ConfigurationTrueColor fromJsonMap(Map map) {
        ConfigurationTrueColor result = new ConfigurationTrueColor(0);
        try {
            result.red = (int) (double) map.get("red");
            result.green = (int) (double) map.get("green");
            result.blue = (int) (double) map.get("blue");
        } catch (Exception ex) {
            System.err.println("Exception "+ex+" when reading TrueColor from config");
        }
        return result;
    }
}
