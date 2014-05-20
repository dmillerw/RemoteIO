package dmillerw.remoteio.inventory.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class InventoryArmor implements IInventory {

	private EntityPlayer player;

	private ItemStack[] array;

	public InventoryArmor(EntityPlayer player) {
		this.player = player;
		this.array = player.inventory.armorInventory;
	}
	
	@Override
	public int getSizeInventory() {
		return array.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return array[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (array[slot] != null) {
			ItemStack itemstack;

			if (array[slot].stackSize <= amount) {
				itemstack = array[slot];
				array[slot] = null;
				return itemstack;
			} else {
				itemstack = array[slot].splitStack(amount);

				if (array[slot].stackSize == 0) {
					array[slot] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (array[slot] != null) {
			ItemStack itemstack = array[slot];
			array[slot] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		array[slot] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		int reverse = 3 - slot;
		return stack.getItem().isValidArmor(stack, reverse, player);
	}
	
}
