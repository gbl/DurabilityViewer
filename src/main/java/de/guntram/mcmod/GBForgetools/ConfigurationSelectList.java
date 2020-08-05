/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GBForgetools;

/**
 *
 * @author gbl
 */
public class ConfigurationSelectList extends ConfigurationItem {
    
    final String[] options;
    
    public ConfigurationSelectList(String key, String toolTip, String[] options, Object value, Object defaultValue) {
        super(key, toolTip, value, defaultValue);
        this.options = options;
    }
}
