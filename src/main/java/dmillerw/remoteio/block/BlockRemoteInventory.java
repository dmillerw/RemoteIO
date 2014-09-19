package dmillerw.remoteio.block;

import dmillerw.remoteio.block.core.BlockIOCore;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.item.ItemWirelessTransmitter;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.core.TileIOCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockRemoteInventory extends BlockIOCore {

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        boolean result = super.onBlockActivated(world, x, y, z, player, side, fx, fy, fz);
        if (result) {
            return result;
        }

        TileRemoteInventory tile = (TileRemoteInventory) world.getTileEntity(x, y, z);

        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == HandlerItem.wirelessTransmitter) {
            if (!world.isRemote) {
                EntityPlayer player1 = ItemWirelessTransmitter.getPlayer(player.getCurrentEquippedItem());

                if (player1 != null) {
                    tile.setPlayer(player1);
                    player.addChatComponentMessage(new ChatComponentTranslation("chat.target.load"));
                    return true;
                }
            }
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
