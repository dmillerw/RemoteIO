package com.dmillerw.remoteIO.core.helper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class StackHelper {

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
	
}
