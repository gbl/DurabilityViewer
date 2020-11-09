package de.guntram.mcmod.durabilityviewer.event;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
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
                        I18n.format("tooltip.durability", 
				(itemStack.getMaxDamage() - itemStack.getDamage())+
				" / "+
				itemStack.getMaxDamage()
                        );
                if (!event.getToolTip().contains(toolTip)) {
                    event.getToolTip().add(new StringTextComponent(toolTip));
                }
            }
        }
        
        if (Screen.hasAltDown()) {      // hasAltDown
            CompoundNBT tag=event.getItemStack().getTag();
            if (tag != null) {
                addCompoundTag("", event.getToolTip(), tag);
            }
        }
    }
    
    private void addCompoundTag(String prefix, List<ITextComponent>list, CompoundNBT tag) {
        TreeSet<String> sortedKeys = new TreeSet(tag.keySet());
        for (String key: sortedKeys) {
            INBT elem=tag.get(key);
            switch(elem.getId()) {
                case 2: list.add(new StringTextComponent(prefix+key+": §2"+tag.getShort(key))); break;
                case 3: list.add(new StringTextComponent(prefix+key+": §3"+tag.getInt(key))); break;
                case 6: list.add(new StringTextComponent(prefix+key+": §6"+tag.getDouble(key))); break;
                case 8: list.add(new StringTextComponent(prefix+key+": §8"+tag.getString(key))); break;
                case 9: list.add(new StringTextComponent(prefix+key+": §9List, "+((ListNBT)elem).size()+" items")); break;
                case 10:list.add(new StringTextComponent(prefix+key+": §aCompound"));
                        if (Screen.hasShiftDown()) {      // hasShiftDown
                            addCompoundTag(prefix+"    ", list, (CompoundNBT)elem);
                        }
                        break;
                default:list.add(new StringTextComponent(prefix+key+": Type "+elem.getId())); break;
            }
        }
    }    
}
