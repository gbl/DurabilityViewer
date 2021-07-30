package de.guntram.mcmod.durabilityviewer.event;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
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
                        I18n.get("tooltip.durability", 
				(itemStack.getMaxDamage() - itemStack.getDamageValue())+
				" / "+
				itemStack.getMaxDamage()
                        );
                if (!event.getToolTip().contains(toolTip)) {
                    event.getToolTip().add(new TextComponent(toolTip));
                }
            }
        }
        
        if (Screen.hasAltDown()) {      // hasAltDown
            CompoundTag tag=event.getItemStack().getTag();
            if (tag != null) {
                addCompoundTag("", event.getToolTip(), tag);
            }
        }
    }
    
    private void addCompoundTag(String prefix, List<Component>list, CompoundTag tag) {
        TreeSet<String> sortedKeys = new TreeSet(tag.getAllKeys());
        for (String key: sortedKeys) {
            Tag elem=tag.get(key);
            switch(elem.getId()) {
                case 2: list.add(new TextComponent(prefix+key+": §2"+tag.getShort(key))); break;
                case 3: list.add(new TextComponent(prefix+key+": §3"+tag.getInt(key))); break;
                case 6: list.add(new TextComponent(prefix+key+": §6"+tag.getDouble(key))); break;
                case 8: list.add(new TextComponent(prefix+key+": §8"+tag.getString(key))); break;
                case 9: list.add(new TextComponent(prefix+key+": §9List, "+((ListTag)elem).size()+" items")); break;
                case 10:list.add(new TextComponent(prefix+key+": §aCompound"));
                        if (Screen.hasShiftDown()) {      // hasShiftDown
                            addCompoundTag(prefix+"    ", list, (CompoundTag)elem);
                        }
                        break;
                default:list.add(new TextComponent(prefix+key+": Type "+elem.getId())); break;
            }
        }
    }    
}
