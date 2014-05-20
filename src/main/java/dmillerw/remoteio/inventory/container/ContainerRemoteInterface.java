package dmillerw.remoteio.inventory.container;

import dmillerw.remoteio.inventory.container.core.ContainerIO;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * @author dmillerw
 */
public class ContainerRemoteInterface extends ContainerIO {

	public ContainerRemoteInterface(InventoryPlayer inventoryPlayer, TileRemoteInterface tile) {
		super(inventoryPlayer, tile);
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int id) {
		if (!player.worldObj.isRemote) {
			switch(id) {
				case 0: ((TileRemoteInterface)tile).updateRotation(-1); break;
				case 1: ((TileRemoteInterface)tile).updateRotation( 1); break;
			}

			return true;
		}

		return false;
	}

}
