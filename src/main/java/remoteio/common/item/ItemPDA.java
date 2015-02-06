package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.handler.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import remoteio.common.lib.ModInfo;

/**
 * @author dmillerw
 */
public class ItemPDA
extends Item {
    public ItemPDA() {
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        entityPlayer.openGui(RemoteIO.instance, GuiHandler.GUI_PDA, world, 0, 0, 0);
        return itemStack;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "pda");
    }
}
