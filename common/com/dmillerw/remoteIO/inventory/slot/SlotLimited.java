package com.dmillerw.remoteIO.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotLimited extends Slot {

	public Class<? extends Item> limiter;
	
	public SlotLimited(IInventory inventory, int x, int y, int z, Class<? extends Item> limiter) {
		super(inventory, x, y, z);
		
		this.limiter = limiter;
	}

	public boolean isItemValid(ItemStack stack) {
		return (this.limiter != null ? (limiter.isAssignableFrom(stack.getItem().getClass())) : false);
	}
	
}
