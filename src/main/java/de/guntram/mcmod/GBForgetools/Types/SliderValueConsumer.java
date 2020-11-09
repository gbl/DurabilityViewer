/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.GBForgetools.Types;

/**
 *
 * @author gbl
 */
public interface SliderValueConsumer {
    public void onConfigChanging(String option, Object value);
    public boolean wasMouseReleased();
    public void setMouseReleased(boolean value);
}
