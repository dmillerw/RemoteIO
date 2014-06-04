package appeng.api.implementations;

import appeng.api.crafting.ICraftingPatternMAC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Implemented on {@link Item}
 */
public interface ICraftingPatternItem
{

	/**
	 * Get information about the contents of a crafting pattern.
	 * 
	 * @param is
	 *            potential crafting pattern.
	 * @return the MAC Crafting Pattern, or null
	 */
	ICraftingPatternMAC getPatternForItem(ItemStack is);
}
