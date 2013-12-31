package com.dmillerw.remoteIO.core.helper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackHelper {

	public static ItemStack resize(ItemStack stack, int size) {
		ItemStack stack2 = stack.copy();
		stack2.stackSize = size;
		if (stack2.stackSize > stack2.getMaxStackSize()) {
			stack2.stackSize = stack2.getMaxStackSize();
		}
		return stack2;
	}
	
	public static boolean areEqual(ItemStack item1, ItemStack item2, boolean compareNBT) {
		if (item1 != null && item2 != null) {
			boolean flag = true;
			
			if (item1.itemID != item2.itemID) {
				flag = false;
			}
			
			if (item1.getItemDamage() != item2.getItemDamage() && (item1.getItemDamage() != OreDictionary.WILDCARD_VALUE && item2.getItemDamage() != OreDictionary.WILDCARD_VALUE)) {
				flag = false;
			}
			
			if (compareNBT) {
				if (!ItemStack.areItemStackTagsEqual(item1, item2)) {
					flag = false;
				}
			}
			
			return flag;
		}
		
		return false;
	}
	
	public static ItemStack consumeItem(ItemStack stack) {
		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem()) {
				return stack.getItem().getContainerItemStack(stack);
			} else {
				return null;
			}
		} else {
			stack.splitStack(1);
			return stack;
		}
	}
	
}
