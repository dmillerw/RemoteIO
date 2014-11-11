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
    }
}
