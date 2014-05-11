package appeng.api.implementations.items;

import appeng.api.config.Upgrades;
import net.minecraft.item.ItemStack;

public interface IUpgradeModule
{

	/**
	 * @param itemstack
	 * @return null, or a valid upgrade type.
	 */
	Upgrades getType(ItemStack itemstack);

}
