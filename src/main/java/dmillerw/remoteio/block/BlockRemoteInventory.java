package dmillerw.remoteio.block;

import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.item.ItemWirelessTransmitter;
import dmillerw.remoteio.lib.ModItems;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockRemoteInventory extends BlockIOCore {

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("targetPlayer")) {
            TileRemoteInventory tileRemoteInventory = (TileRemoteInventory) world.getTileEntity(x, y, z);
            if (tileRemoteInventory != null) {
                tileRemoteInventory.setPlayer(itemStack.getTagCompound().getString("targetPlayer"));
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        boolean result = super.onBlockActivated(world, x, y, z, player, side, fx, fy, fz);
        if (result) {
            return result;
        }

        TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

        if (tile.target == null || tile.target.isEmpty()) {
            if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ModItems.wirelessTransmitter) {
                if (!world.isRemote) {
                    EntityPlayer player1 = ItemWirelessTransmitter.getPlayer(player.getCurrentEquippedItem());

                    if (player1 != null) {
                        tile.setPlayer(player1.getCommandSenderName());
                        player.addChatComponentMessage(new ChatComponentTranslation("chat.target.load"));
                        return true;
                    }
                }
            }
        } else {
            return false;
        }

        return result;
    }

    @Override
    public int getGuiID() {
        return GuiHandler.GUI_REMOTE_INVENTORY;
    }

    @Override
    public TileIOCore getTileEntity() {
        return new TileRemoteInventory();
    }
}
