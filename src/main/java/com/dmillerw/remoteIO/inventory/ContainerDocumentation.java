package com.dmillerw.remoteIO.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDocumentation extends Container {

    private InventoryBasic holdSlot = new InventoryBasic("Hold Slot", false, 1);

	private final EntityPlayer player;

	public ContainerDocumentation(EntityPlayer player) {
		this.player = player;

        this.addSlotToContainer(new Slot(holdSlot, 0, 174, 8));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
	}

    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (holdSlot.getStackInSlot(0) != null) {
            player.dropPlayerItem(holdSlot.getStackInSlot(0));
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			//TODO Merge handling

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
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
	
	// Merge method that obeys stack size limit
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

				if (slotStack != null && slotStack.itemID == itemStack.itemID && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
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

				if (slotStack == null) {
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
	
}
