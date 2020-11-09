/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GBForgetools;

import java.util.function.Consumer;

/**
 *
 * @author gbl
 */
public class ConfigurationItem {
    
    String key;
    String toolTip;
    private Object value;
    Object defaultValue;
    Object minValue, maxValue;
    Consumer<Object> changeHandler;
    
    public ConfigurationItem(String key, String toolTip, Object value, Object defaultValue) {
        this(key, toolTip, value, defaultValue, null, null);
    }
    public ConfigurationItem(String key, String toolTip, Object value, Object defaultValue, Object minValue, Object maxValue) {
        this(key, toolTip, value, defaultValue, minValue, maxValue, null);
    }
    
    public ConfigurationItem(String key, String toolTip, Object value, Object defaultValue, Object minValue, Object maxValue, Consumer<Object> handler) {
        this.key = key;
        this.toolTip = toolTip;
        this.value = value;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.changeHandler = handler;
    }
    
    public void setValue(Object o) {
        this.value = o;
        if (changeHandler != null) {
            changeHandler.accept(o);
        }
    }
    
    public Object getValue() {
        return value;
    }
}
