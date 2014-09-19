package dmillerw.remoteio.inventory.container;

import dmillerw.remoteio.inventory.container.core.ContainerIO;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author dmillerw
 */
public class ContainerRemoteInventory extends ContainerIO {

    public ContainerRemoteInventory(InventoryPlayer inventoryPlayer, TileRemoteInventory tile) {
        super(inventoryPlayer, tile);

        if (!tile.getWorldObj().isRemote) {
            tile.sendAccessType();
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (!player.worldObj.isRemote) {
            switch (id) {
                case 0:
                    if (((TileRemoteInventory) tile).accessType == 0)
                        ((TileRemoteInventory) tile).accessType = 1;
                    else if (((TileRemoteInventory) tile).accessType == 1)
                        ((TileRemoteInventory) tile).accessType = 0;
                    ((TileRemoteInventory) tile).sendAccessType();
                    break;
            }

            return true;
        }

        return false;
    }
}
