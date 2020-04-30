package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class TooltipMixin {
    
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract boolean isDamaged();
    @Shadow public abstract int getMaxDamage();
    @Shadow public abstract int getDamage();
    @Shadow public abstract CompoundTag getTag();
    
//    @Inject(method="getTooltip(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List",
    @Inject(method="getTooltip",            
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, 
            CallbackInfoReturnable<List> ci,
            List<Text> list) {
        
        if (!advanced.isAdvanced() && !this.isEmpty()) {
            if (this.isDamaged()) {
                Text toolTip = new LiteralText(I18n.translate("tooltip.durability",
                        (this.getMaxDamage()- this.getDamage())+
                        " / "+
                        this.getMaxDamage()));
                toolTip=toolTip.formatted(ConfigurationHandler.getTooltipColor());
                if (!list.contains(toolTip)) {
                    list.add(toolTip);
                }
            }
        }

        if (Screen.hasAltDown()) {
            CompoundTag tag=this.getTag();
            if (tag != null) {
                addTag("", list, tag, "");
            }
        }
    }
    
    private void addTag(String prefix, List<Text>list, Tag tag, String tagName) {
        if (tag instanceof CompoundTag) {
            list.add(new LiteralText(prefix+tagName+": §aCompound"));
            if (prefix.isEmpty() || Screen.hasShiftDown()) {
                TreeSet<String> sortedKeys = new TreeSet(((CompoundTag)tag).getKeys());
                for (String key: sortedKeys) {
                    addTag(prefix+"   ", list, ((CompoundTag) tag).get(key), key);
                }
            }
        } else if (tag instanceof ListTag) {
            if (prefix.isEmpty() || Screen.hasShiftDown()) {
                list.add(new LiteralText(prefix+tagName+": §9List"+((ListTag)tag).size()+" items"));
                for (int i=0; i<((ListTag) tag).size(); i++) {
                    addTag(prefix+"   ", list, ((ListTag) tag).get(i), Integer.toString(i));
                }
            }
        } else {
            list.add(new LiteralText(prefix).append(tagName).append(": ").append(tag.toText()));
        }
    }
}