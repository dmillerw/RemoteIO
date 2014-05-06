package dmillerw.remoteio.inventory;

import dmillerw.remoteio.block.tile.TileRemoteInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * @author dmillerw
 */
public class ContainerRemoteInterface extends Container {

	private final TileRemoteInterface tile;

	public ContainerRemoteInterface(InventoryPlayer inventoryPlayer, TileRemoteInterface tile) {
		this.tile = tile;

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(tile.upgrades, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
