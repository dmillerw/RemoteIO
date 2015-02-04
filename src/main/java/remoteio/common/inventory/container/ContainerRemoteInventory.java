package remoteio.common.inventory.container;

import remoteio.common.inventory.container.core.ContainerIO;
import remoteio.common.tile.TileRemoteInventory;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author dmillerw
 */
public class ContainerRemoteInventory extends ContainerIO {

    public ContainerRemoteInventory(InventoryPlayer inventoryPlayer, TileRemoteInventory tile) {
        super(inventoryPlayer, tile);
    }
}
