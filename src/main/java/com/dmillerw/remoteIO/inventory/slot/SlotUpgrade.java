package com.dmillerw.remoteIO.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

public class SlotUpgrade extends Slot {

    private final int machineType;
    
	public SlotUpgrade(IInventory inventory, int id, int y, int z, int machineType) {
		super(inventory, id, y, z);
		
		this.machineType = machineType;
	}

	public boolean isItemValid(ItemStack stack) {
	    if (stack == null) {
	        return false;
	    }

	    Upgrade upgrade = Upgrade.values()[stack.getItemDamage()];
	    
	    if (upgrade == null) {
	        return false;
	    }
	    
	    return upgrade.enabled && upgrade.validMachines[this.machineType];
	}
	
}
