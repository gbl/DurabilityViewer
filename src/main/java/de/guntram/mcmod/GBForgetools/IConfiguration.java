/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GBForgetools;

import java.util.List;

/**
 *
 * @author gbl
 */
public interface IConfiguration {
    
    public List<String> getKeys();
    public Object getValue(String option);
    public boolean setValue(String option, Object value);
    
    public Object getDefault(String option);
    public Object getMin(String option);
    public Object getMax(String option);
    public String getTooltip(String option);

    boolean isSelectList(String option);
    String[] getListOptions(String option);
}
