package com.dmillerw.remoteIO.inventory.slot;

import com.dmillerw.remoteIO.item.ItemUpgrade;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

public class SlotUpgrade extends Slot {

    private final int machineType;
    
	public SlotUpgrade(IInventory inventory, int id, int x, int y, int machineType) {
		super(inventory, id, x, y);
		
		this.machineType = machineType;
	}

	public boolean isItemValid(ItemStack stack) {
	    if (stack == null) {
	        return false;
	    }

        if (!(stack.getItem() instanceof ItemUpgrade)) {
            return false;
        }

	    Upgrade upgrade = Upgrade.values()[stack.getItemDamage()];
	    
	    if (upgrade == null) {
	        return false;
	    }
	    
	    return upgrade.enabled && upgrade.validMachines[this.machineType];
	}
	
}
