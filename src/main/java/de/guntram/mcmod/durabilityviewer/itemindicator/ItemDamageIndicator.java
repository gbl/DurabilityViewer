package de.guntram.mcmod.durabilityviewer.itemindicator;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.item.ItemStack;

public class ItemDamageIndicator implements ItemIndicator {

    final ItemStack stack;
    public ItemDamageIndicator(ItemStack stack) {
        this.stack=stack;
    }

    @Override
    public String getDisplayValue() {
        int max=stack.getDurability();
        int cur=stack.getDurability()-stack.getDamage();
        int shown;
        if (cur > max*ConfigurationHandler.showDamageOverPercent()/100) {
            shown=-stack.getDamage();
        } else {
            shown=cur;
        }
        return String.valueOf(shown);
    }

    @Override
    public int getDisplayColor() {
        int max=stack.getDurability();
        int cur=stack.getDamage();
        if (cur < max/5)
            return color_green;
        if (cur > max*9/10 && cur>max-100)
            return color_red;
        if (cur > max*4/5 && cur>max-200)
            return color_yellow;
        return color_white;
    }
    
    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public boolean isItemStackDamageable() {
        return stack.hasDurability();
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
    
}
