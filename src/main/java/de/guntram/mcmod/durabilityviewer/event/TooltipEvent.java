package de.guntram.mcmod.durabilityviewer.event;

import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TooltipEvent
{
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent event) {
        if (!event.getFlags().isAdvanced() && !event.getItemStack().isEmpty()) {
            final ItemStack itemStack = event.getItemStack();
            if (itemStack.isDamaged()) {
                final String toolTip = ConfigurationHandler.getInstance().getTooltipColor() +
                        I18n.format("tooltip.durability")+
                        (itemStack.getMaxDamage() - itemStack.getDamage())+
                        " / "+
                        itemStack.getMaxDamage()
                        ;
                if (!event.getToolTip().contains(toolTip)) {
                    event.getToolTip().add(new StringTextComponent(toolTip));
                }
            }
        }
    }
}
