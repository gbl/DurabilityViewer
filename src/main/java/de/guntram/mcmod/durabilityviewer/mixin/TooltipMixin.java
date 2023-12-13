package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
    @Shadow public abstract NbtCompound getNbt();
    
//    @Inject(method="getTooltip(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List",
    @Inject(method="getTooltip",            
            at=@At("RETURN"), locals=LocalCapture.CAPTURE_FAILHARD)
    private void getTooltipdone(PlayerEntity playerIn, TooltipContext advanced, 
            CallbackInfoReturnable<List> ci,
            List<Text> list) {
        
        if (!advanced.isAdvanced() && !this.isEmpty()) {
            if (this.isDamaged()) {
                Text toolTip = Text.literal(I18n.translate("tooltip.durability",
                        (this.getMaxDamage()- this.getDamage())+
                        " / "+
                        this.getMaxDamage()))
                .formatted(ConfigurationHandler.getTooltipColor());
                if (!list.contains(toolTip)) {
                    list.add(toolTip);
                }
            }
        }

        if (Screen.hasAltDown()) {
            NbtCompound tag=this.getNbt();
            if (tag != null) {
                addNbtCompound("", list, tag);
            }
        }
    }
    
    private void addNbtCompound(String prefix, List<Text>list, NbtCompound tag) {
        TreeSet<String> sortedKeys = new TreeSet(tag.getKeys());
        for (String key: sortedKeys) {
            NbtElement elem=tag.get(key);
            switch(elem.getType()) {
                case 2: list.add(Text.literal(prefix+key+": §2"+tag.getShort(key))); break;
                case 3: list.add(Text.literal(prefix+key+": §3"+tag.getInt(key))); break;
                case 6: list.add(Text.literal(prefix+key+": §6"+tag.getDouble(key))); break;
                case 8: list.add(Text.literal(prefix+key+": §8"+tag.getString(key))); break;
                case 9: list.add(Text.literal(prefix+key+": §9List, "+((NbtList)elem).size()+" items")); break;
                case 10:list.add(Text.literal(prefix+key+": §aCompound"));
                        if (Screen.hasShiftDown()) {
                            addNbtCompound(prefix+"    ", list, (NbtCompound)elem);
                        }
                        break;
                default:list.add(Text.literal(prefix+key+": Type "+elem.getType())); break;
            }
        }
    }
}
