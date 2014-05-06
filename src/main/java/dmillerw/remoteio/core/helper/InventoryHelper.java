package dmillerw.remoteio.core.helper;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author dmillerw
 */
public class InventoryHelper {

	public static ItemStack[] toArray(IInventory inventory) {
		ItemStack[] items = new ItemStack[inventory.getSizeInventory()];
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null) {
				items[i] = stack;
			}
		}
		return items;
	}

	public static boolean containsStack(IInventory inventory, ItemStack stack, boolean compareMeta, boolean compareNBT) {
		ItemStack[] items = toArray(inventory);

		for (int i=0; i<items.length; i++) {
			ItemStack item = items[i];

			if (item != null) {
				if (stack.getItem() == item.getItem()) {
					if (!compareMeta || (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || item.getItemDamage() == OreDictionary.WILDCARD_VALUE) || stack.getItemDamage() == item.getItemDamage()) {
						if (!compareNBT || (ItemStack.areItemStacksEqual(stack, item))) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

}
