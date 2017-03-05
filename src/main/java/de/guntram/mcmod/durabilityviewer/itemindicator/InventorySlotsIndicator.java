package de.guntram.mcmod.durabilityviewer.itemindicator;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventorySlotsIndicator implements ItemIndicator {

    final ItemStack stack;
    final int emptySlots;
    
    public InventorySlotsIndicator(InventoryPlayer inventory) {
        stack=new ItemStack(Item.getItemById(54));
        int slots = 0;
        for (final ItemStack invitems : inventory.mainInventory) {
            if (invitems.isEmpty()) {
                slots++;
            }
        }
        emptySlots=slots;
    }
    

    @Override
    public String getDisplayValue() {
        return String.valueOf(emptySlots);
    }

    @Override
    public int getDisplayColor() {
        return color_white;
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
        return stack;
    }
}
