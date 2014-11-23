package dmillerw.remoteio.item;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.handler.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ItemPDA extends Item {

    public ItemPDA() {
        super();

        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        entityPlayer.openGui(RemoteIO.instance, GuiHandler.GUI_PDA, world, 0, 0, 0);
        return itemStack;
    }
}
