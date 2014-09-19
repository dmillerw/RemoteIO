package dmillerw.remoteio.inventory.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class InventoryArray implements IInventory {

    private ItemStack[] array;

    public InventoryArray(ItemStack[] array) {
        this.array = array;
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
        return 64;
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
        return true;
    }
}
