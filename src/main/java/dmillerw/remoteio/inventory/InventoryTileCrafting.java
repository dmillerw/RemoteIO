package dmillerw.remoteio.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class InventoryTileCrafting extends InventoryCrafting {

    private ItemStack[] stackList;
    private int inventoryWidth;
    private Container eventHandler;

    public InventoryTileCrafting(int width, int height) {
        super(null, width, height);
        int size = width * height;
        this.stackList = new ItemStack[size];
        this.inventoryWidth = width;
    }

    public InventoryTileCrafting setContainer(Container container) {
        this.eventHandler = container;
        return this;
    }

    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= this.getSizeInventory() ? null : this.stackList[slot];
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int column) {
        if (row >= 0 && row < this.inventoryWidth) {
            int k = row + column * this.inventoryWidth;
            return this.getStackInSlot(k);
        } else {
            return null;
        }
    }

    @Override
    public String getInventoryName() {
        return "container.crafting";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.stackList[slot] != null) {
            ItemStack itemstack = this.stackList[slot];
            this.stackList[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.stackList[slot] != null) {
            ItemStack itemstack;

            if (this.stackList[slot].stackSize <= amount) {
                itemstack = this.stackList[slot];
                this.stackList[slot] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            } else {
                itemstack = this.stackList[slot].splitStack(amount);

                if (this.stackList[slot].stackSize == 0) {
                    this.stackList[slot] = null;
                }

                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.stackList[slot] = stack;
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }
}