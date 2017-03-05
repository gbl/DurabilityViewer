package de.guntram.mcmod.durabilityviewer.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TooltipEvent
{
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent event) {
        if (!event.isShowAdvancedItemTooltips() && !event.getItemStack().isEmpty()) {
            final ItemStack itemStack = event.getItemStack();
            if (itemStack.isItemDamaged()) {
                final String toolTip = ConfigurationHandler.getInstance().getTooltipColor() +
                        I18n.format("tooltip.durability")+
                        (itemStack.getMaxDamage() - itemStack.getItemDamage())+
                        " / "+
                        itemStack.getMaxDamage()
                        ;
                if (!event.getToolTip().contains(toolTip)) {
                    event.getToolTip().add(toolTip);
                }
            }
        }
    }
}
