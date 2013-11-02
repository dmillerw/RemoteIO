package com.dmillerw.remoteIO.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.inventory.slot.SlotUpgrade;

public class ContainerIOUpgrade extends Container {

	private final EntityPlayer player;
	
	private final TileEntityIO tile;
	
	public ContainerIOUpgrade(EntityPlayer player, TileEntityIO tile) {
		this.player = player;
		this.tile = tile;
		
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new SlotUpgrade(tile.upgrades, i, 8 + i * 18, 17));
		}
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
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

			if (slotID >= 0 && slotID <= 9) {
				if (!this.mergeItemStack(itemstack1, 10, 45, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
				return null;
			}

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
	
}
