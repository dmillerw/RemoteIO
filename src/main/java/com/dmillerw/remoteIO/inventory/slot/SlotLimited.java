package com.dmillerw.remoteIO.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLimited extends Slot {

    private final Class limiterClass;

	public SlotLimited(IInventory inventory, int id, int x, int y, Class limiterClass) {
		super(inventory, id, x, y);
		
        this.limiterClass = limiterClass;
	}

	public boolean isItemValid(ItemStack stack) {
	    if (stack == null) {
	        return false;
	    }

	    return limiterClass.isAssignableFrom(stack.getItem().getClass());
	}
	
}
