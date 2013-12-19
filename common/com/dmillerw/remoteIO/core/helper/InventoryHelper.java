package com.dmillerw.remoteIO.core.helper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

public class InventoryHelper {

	public static ItemStack[] getContents(IInventory inventory) {
		ItemStack[] contents = new ItemStack[inventory.getSizeInventory()];
		
		for (int i=0; i<contents.length; i++) {
			contents[i] = inventory.getStackInSlot(i);
		}
		
		return contents;
	}
	
	public static boolean inventoryContains(IInventory inventory, ItemStack stack, boolean compareNBT) {
		for (ItemStack item : getContents(inventory)) {
			if (StackHelper.areEqual(item, stack, false)) {
				return true;
			}
		}
		
		return false;
	}

	public static int amountContained(IInventory inventory, ItemStack stack, boolean check) {
		int count = 0;
		
		for (ItemStack item : getContents(inventory)) {
			if (StackHelper.areEqual(item, stack, false)) {
				count += item.stackSize;
			}
		}
		
		return count;
	}
	
	public static void writeToNBT(IInventory inventory, NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			
			if (stack != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				stack.writeToNBT(item);
				list.appendTag(item);
			}
		}
		
		nbt.setTag("Items", list);
	}
	
	public static ItemStack[] readFromNBT(IInventory inventory, NBTTagCompound nbt) {
		NBTTagList list = nbt.getTagList("Items");
		ItemStack[] items = new ItemStack[inventory.getSizeInventory()];
		
		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound) list.tagAt(i);
			byte slot = item.getByte("Slot");
			items[slot] = ItemStack.loadItemStackFromNBT(item);
		}
		
		return items;
	}
	
}
