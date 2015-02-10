package remoteio.common.block.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemBlockRemoteInventory extends ItemBlock {
    public ItemBlockRemoteInventory(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean debug) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("targetPlayer")) {
            list.add(String.format(StatCollector.translateToLocal("tooltip.bound"), itemStack.getTagCompound().getString("targetPlayer")));
        } else {
            list.add(StatCollector.translateToLocal("tooltip.inventory.creative"));
        }
    }
}