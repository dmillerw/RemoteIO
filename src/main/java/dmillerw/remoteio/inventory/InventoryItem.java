package dmillerw.remoteio.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author dmillerw
 */
public class InventoryItem implements IInventory {

    private final ItemStack stack;

    private ItemStack[] inv;

    private int stackSize = 64;

    public InventoryItem(ItemStack stack, int slotCount) {
        this.stack = stack;
        this.inv = new ItemStack[slotCount];

        readFromNBT();
    }

	private InventoryItem(ItemStack stack, int slotCount, int stackSize) {
		this.stack = stack;
		this.inv = new ItemStack[slotCount];
		this.stackSize = stackSize;

		readFromNBT();
	}

    public ItemStack getStack() {
        return stack.copy();
    }

    public void readFromNBT() {
        readFromNBT(stack.getTagCompound());
    }

    private void readFromNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            stack.setTagCompound(new NBTTagCompound());
            nbt = stack.getTagCompound();
        }

        NBTTagList itemList = nbt.getTagList("Items", 10);

        for (int i = 0; i < itemList.tagCount(); ++i) {
            NBTTagCompound tag = itemList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < this.inv.length) {
                this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    public void writeToNBT() {
        writeToNBT(stack.getTagCompound());
    }

    private void writeToNBT(NBTTagCompound nbt) {
        if (nbt == null) {
            stack.setTagCompound(new NBTTagCompound());
            nbt = stack.getTagCompound();
        }

        NBTTagList list = new NBTTagList();

        for (int i = 0; i < this.inv.length; ++i) {
            if (this.inv[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                this.inv[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }

        nbt.setTag("Items", list);
    }

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (this.inv[slot] != null) {
            ItemStack itemstack;

            if (this.inv[slot].stackSize <= count) {
                itemstack = this.inv[slot];
                this.inv[slot] = null;
                return itemstack;
            } else {
                itemstack = this.inv[slot].splitStack(count);

                if (this.inv[slot].stackSize == 0) {
                    this.inv[slot] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inv[slot] != null) {
            ItemStack itemstack = this.inv[slot];
            this.inv[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inv[slot] = stack;

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
        return stackSize;
    }

    @Override
    public void markDirty() {
        writeToNBT();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
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