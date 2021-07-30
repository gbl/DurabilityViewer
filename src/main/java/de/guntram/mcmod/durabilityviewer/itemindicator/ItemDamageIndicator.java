package de.guntram.mcmod.durabilityviewer.itemindicator;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.world.item.ItemStack;

public class ItemDamageIndicator implements ItemIndicator {

    final ItemStack stack;
    public ItemDamageIndicator(ItemStack stack) {
        this.stack=stack;
    }

    @Override
    public String getDisplayValue() {
        if (!(stack.isDamageableItem())) {
            return "";
        }
        int max=stack.getMaxDamage();
        int cur=stack.getMaxDamage()-stack.getDamageValue();
        int shown;
        if (cur > max*ConfigurationHandler.showDamageOverPercent()/100) {
            shown=-stack.getDamageValue();
        } else {
            shown=cur;
        }
        if (ConfigurationHandler.getShowPercentValues()) {
            return String.format("%.1f%%", shown * 100.0 / stack.getMaxDamage());
        }
        return String.valueOf(shown);
    }

    @Override
    public int getDisplayColor() {
        int max=stack.getMaxDamage();
        int cur=stack.getDamageValue();
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
        return stack.isDamageableItem();
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }
    
}
