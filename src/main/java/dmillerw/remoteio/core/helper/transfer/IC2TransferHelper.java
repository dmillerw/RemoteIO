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

	public static int getCharge(IInventory inventory) {
		int charge = 0;
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() instanceof IElectricItem) {
				charge += ElectricItem.manager.getCharge(stack);
			}
		}
		return charge;
	}

	/** Returns amount NOT used */
	public static int fill(IInventory inventory, int amount) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() instanceof IElectricItem) {
				int chargeAmount = ElectricItem.manager.charge(stack, amount, ((IElectricItem)stack.getItem()).getTier(stack), false, true);
				if (amount - chargeAmount >= 0) {
					amount -= ElectricItem.manager.charge(stack, (int) amount, ((IElectricItem)stack.getItem()).getTier(stack), false, false);
				} else {
					break;
				}
			}
		}
		return amount;
	}

	public static int drain(IInventory inventory, int amount) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() instanceof IElectricItem) {
				int dischargeAmount = ElectricItem.manager.discharge(stack, amount, ((IElectricItem)stack.getItem()).getTier(stack), false, true);
				if (amount - dischargeAmount >= 0) {
					amount -= ElectricItem.manager.discharge(stack, amount, ((IElectricItem)stack.getItem()).getTier(stack), false, false);
				} else {
					break;
				}
			}
		}

		return getCharge(inventory);
	}
}
