package dmillerw.remoteio.core.helper.transfer;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class IC2TransferHelper {

	public static boolean requiresCharge(IInventory inventory) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() instanceof IElectricItem && ElectricItem.manager.charge(stack, 1, ((IElectricItem)stack.getItem()).getTier(stack), true, true) == 1) {
				return true;
			}
		}
		return false;
	}

	/** Returns amount NOT used */
	public static double fill(IInventory inventory, double amount) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() instanceof IElectricItem) {
				amount -= ElectricItem.manager.charge(stack, (int) Math.floor(amount), ((IElectricItem)stack.getItem()).getTier(stack), false, false);
			}
		}
		return amount;
	}

}
