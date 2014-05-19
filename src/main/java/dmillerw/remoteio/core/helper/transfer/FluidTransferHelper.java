package dmillerw.remoteio.core.helper.transfer;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmillerw
 */
public class FluidTransferHelper {

	public static int fill(IInventory inventory, FluidStack resource, boolean doFill) {
		int filled = 0;
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isEmptyContainer(stack)) {
				FluidStack copied = resource.copy();

				ItemStack filledStack = FluidContainerRegistry.fillFluidContainer(copied, stack);

				if (filledStack != null) {
					resource.amount -= copied.amount;
					filled += copied.amount;

					if (doFill) {
						inventory.setInventorySlotContents(i, filledStack);
					}
				}
			}
		}
		return filled;
	}

	public static FluidStack drain(IInventory inventory, FluidStack resource, boolean doDrain) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isFilledContainer(stack)) {
				FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);

				if (fluidStack.isFluidEqual(resource) && fluidStack.amount <= resource.amount) {
					if (doDrain) {
						if (stack.stackSize == 1) {
							if (stack.getItem().hasContainerItem(stack)) {
								inventory.setInventorySlotContents(i, stack.getItem().getContainerItem(stack));
							} else {
								inventory.setInventorySlotContents(i, null);
							}
						} else {
							stack.splitStack(1);
						}
					}
					return fluidStack;
				}
			}
		}
		return null;
	}

	public static FluidStack drain(IInventory inventory, int maxDrain, boolean doDrain) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isFilledContainer(stack)) {
				FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);

				if (fluidStack.amount <= maxDrain) {
					if (doDrain) {
						if (stack.stackSize == 1) {
							if (stack.getItem().hasContainerItem(stack)) {
								inventory.setInventorySlotContents(i, stack.getItem().getContainerItem(stack));
							} else {
								inventory.setInventorySlotContents(i, null);
							}
						} else {
							stack.splitStack(1);
						}
					}
					return fluidStack;
				}
			}
		}
		return null;
	}

	public static boolean canFill(IInventory inventory, Fluid fluid) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isEmptyContainer(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean canDrain(IInventory inventory, Fluid fluid) {
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isFilledContainer(stack)) {
				return true;
			}
		}
		return false;
	}

	public static FluidTankInfo[] getTankInfo(IInventory inventory) {
		List<FluidTankInfo> fluids = new ArrayList<FluidTankInfo>();
		for (int i=0; i<inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && FluidContainerRegistry.isContainer(stack)) {
				if (FluidContainerRegistry.isFilledContainer(stack)) {
					fluids.add(new FluidTankInfo(FluidContainerRegistry.getFluidForFilledItem(stack), FluidContainerRegistry.BUCKET_VOLUME));
				} else {
					fluids.add(new FluidTankInfo(null, FluidContainerRegistry.BUCKET_VOLUME));
				}
			}
		}
		return fluids.toArray(new FluidTankInfo[fluids.size()]);
	}

}
