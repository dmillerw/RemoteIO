package com.dmillerw.remoteIO.inventory.slot;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.config.RIOConfiguration;
import com.dmillerw.remoteIO.item.ItemUpgrade;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {

	public SlotUpgrade(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public boolean isItemValid(ItemStack stack) {
		return (stack.getItem() == RemoteIO.instance.config.itemUpgrade);
	}
	
}
