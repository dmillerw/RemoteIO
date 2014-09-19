package dmillerw.remoteio.core.helper.transfer;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class RFTransferHelper {

    public static int getCharge(IInventory inventory) {
        int charge = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof IElectricItem) {
                charge += ((IEnergyContainerItem) stack.getItem()).getEnergyStored(stack);
            }
        }
        return charge;
    }

    public static int getMaxCharge(IInventory inventory) {
        int charge = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof IElectricItem) {
                charge += ((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack);
            }
        }
        return charge;
    }

    /**
     * Returns amount NOT used
     */
    public static int fill(IInventory inventory, int maxAmount, boolean simulate) {
        int used = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof IEnergyContainerItem) {
                if (used < maxAmount) {
                    IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) stack.getItem();
                    int thisUsed = energyContainerItem.receiveEnergy(stack, maxAmount, simulate);
                    used += thisUsed;
                }
            }
        }
        return used;
    }

    public static int drain(IInventory inventory, int maxAmount, boolean simulate) {
        int extracted = 0;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null && stack.getItem() instanceof IElectricItem) {
                if (extracted < maxAmount) {
                    IEnergyContainerItem energyContainerItem = (IEnergyContainerItem) stack.getItem();
                    extracted += energyContainerItem.extractEnergy(stack, maxAmount, simulate);
                }
            }
        }
        return extracted;
    }
}
