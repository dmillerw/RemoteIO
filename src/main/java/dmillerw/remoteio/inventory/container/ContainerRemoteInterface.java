package dmillerw.remoteio.inventory.container;

import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.inventory.container.slot.SlotLimited;
import dmillerw.remoteio.item.ItemTransferChip;
import dmillerw.remoteio.item.ItemUpgradeChip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ContainerRemoteInterface extends Container {

	private final TileRemoteInterface tile;

	public ContainerRemoteInterface(InventoryPlayer inventoryPlayer, TileRemoteInterface tile) {
		this.tile = tile;

		if (!tile.getWorldObj().isRemote) {
			tile.updateVisualState();
		}

		// Transfer Chip slots
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new SlotLimited(tile.transferChips, i + j * 2, 17 + j * 18, 13 + i * 18, ItemTransferChip.class));
			}
		}
		for (int i = 0; i < 2; ++i) {
			this.addSlotToContainer(new SlotLimited(tile.transferChips, i + 6, 17 + i * 18, 49, ItemTransferChip.class));
		}
		this.addSlotToContainer(new SlotLimited(tile.transferChips, 8, 17, 67, ItemTransferChip.class));

		// Upgrade Chip slots
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new SlotLimited(tile.upgradeChips, i + j * 2, 128 + j * 18, 13 + i * 18, ItemUpgradeChip.class));
			}
		}
		for (int i = 0; i < 2; ++i) {
			this.addSlotToContainer(new SlotLimited(tile.upgradeChips, i + 6, 146 + i * 18, 49, ItemUpgradeChip.class));
		}
		this.addSlotToContainer(new SlotLimited(tile.upgradeChips, 8, 164, 67, ItemUpgradeChip.class));

		// Player inventory
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 18 + j * 18, 161 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 18 + i * 18, 219));
		}
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int id) {
		if (!player.worldObj.isRemote) {
			switch(id) {
				case 0: tile.updateRotation(-1); break;
				case 1: tile.updateRotation( 1); break;
			}

			return true;
		}

		return false;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotID >= 0 && slotID <= 17) {
				if (!this.mergeItemStack(itemstack1, 18, 54, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 18, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	// Merge method that obeys stack size limit and slot validity
	@Override
	protected boolean mergeItemStack(ItemStack itemStack, int slotMin, int slotMax, boolean reverse) {
		boolean returnValue = false;
		int i = slotMin;

		if (reverse) {
			i = slotMax - 1;
		}

		Slot slot;
		if (itemStack.isStackable()) {
			while (itemStack.stackSize > 0 && (!reverse && i < slotMax || reverse && i >= slotMin)) {
				slot = (Slot) this.inventorySlots.get(i);
				ItemStack slotStack = slot.getStack();

				if (slot.isItemValid(itemStack) && slotStack != null && slotStack.getItem() == itemStack.getItem() && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
					int total = slotStack.stackSize + itemStack.stackSize;
					int max = Math.min(itemStack.getMaxStackSize(), slot.getSlotStackLimit());

					if (total <= max) {
						itemStack.stackSize = 0;
						slotStack.stackSize = total;
						slot.onSlotChanged();
						returnValue = true;
					} else if (slotStack.stackSize < max) {
						itemStack.stackSize -= max - slotStack.stackSize;
						slotStack.stackSize = max;
						slot.onSlotChanged();
						returnValue = true;
					}
				}

				if (reverse) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (itemStack.stackSize > 0) {
			if (reverse) {
				i = slotMax - 1;
			} else {
				i = slotMin;
			}

			while (!reverse && i < slotMax || reverse && i >= slotMin) {
				slot = (Slot) this.inventorySlots.get(i);
				ItemStack slotStack = slot.getStack();

				if (slot.isItemValid(itemStack) && slotStack == null) {
					int max = Math.min(itemStack.getMaxStackSize(), slot.getSlotStackLimit());
					max = Math.min(itemStack.stackSize, max);
					ItemStack copy = itemStack.copy();
					copy.stackSize = max;
					slot.putStack(copy);
					slot.onSlotChanged();
					itemStack.stackSize -= max;
					return true;
				}

				if (reverse) {
					--i;
				} else {
					++i;
				}
			}
		}

		return returnValue;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
