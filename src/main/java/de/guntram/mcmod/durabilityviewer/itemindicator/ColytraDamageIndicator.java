/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer.itemindicator;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author gbl
 */
public class ColytraDamageIndicator extends ItemDamageIndicator {

    private static int elytraMaxDamage;
    private static ItemStack newElytra;

    public ColytraDamageIndicator(ItemStack chestItem) {
        super(chestItem);
        if (elytraMaxDamage == 0) {
            newElytra = new ItemStack(Items.ELYTRA);
            elytraMaxDamage = newElytra.getMaxDamage();
        }        
    }
    
    private int getDamage() {
        int damage;
        try {
            damage = stack.getNbt().getCompound("colytra:ElytraUpgrade").getCompound("tag").getInt("Damage");
            return damage;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public String getDisplayValue() {
        return calculateDisplayValue(elytraMaxDamage, getDamage());
    }

    @Override
    public int getDisplayColor() {
        return calculateDisplayColor(elytraMaxDamage, getDamage());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isItemStackDamageable() {
        return true;
    }

    @Override
    public ItemStack getItemStack() {
        return newElytra;
    }
}
