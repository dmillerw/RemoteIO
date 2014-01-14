package com.dmillerw.remoteIO.turtle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import dan200.turtle.api.ITurtleAccess;

public class TurtleInventoryWrapper implements IInventory {

    private final ITurtleAccess turtle;
    
    public TurtleInventoryWrapper(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        return 16;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.turtle.getSlotContents(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (this.getStackInSlot(i) != null) {
            ItemStack itemstack;

            if (this.getStackInSlot(i).stackSize <= j) {
                itemstack = this.getStackInSlot(i);
                this.setInventorySlotContents(i, null);
                this.onInventoryChanged();
                return itemstack;
            } else {
                itemstack = this.getStackInSlot(i).splitStack(j);

                if (this.getStackInSlot(i).stackSize == 0) {
                    this.setInventorySlotContents(i, null);
                }

                this.onInventoryChanged();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.turtle.setSlotContents(i, itemstack);
    }

    @Override
    public String getInvName() {
        return "Turtle";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void onInventoryChanged() {
        
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openChest() {
        
    }

    @Override
    public void closeChest() {
        
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }
    
}
