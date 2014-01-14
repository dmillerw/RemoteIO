package com.dmillerw.remoteIO.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.inventory.slot.SlotUpgrade;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

public class ContainerUpgrade extends Container {

    private final EntityPlayer player;
    
    private final IInventory upgrades;
    private final IInventory camo;
    
    private final int machineType;
    
    public ContainerUpgrade(EntityPlayer player, IInventory upgrades, IInventory camo, int machineType) {
        this.player = player;
        this.upgrades = upgrades;
        this.camo = camo;
        this.machineType = machineType;
        
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new SlotUpgrade(this.upgrades, i, 8 + i * 18, 17, this.machineType));
        }
        
        this.addSlotToContainer(new Slot(this.camo, 0, 152, 55));
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (itemstack1.getItem() instanceof ItemUpgrade) {
                if (slotID >= 0 && slotID <= 9) {
                    if (!this.mergeItemStack(itemstack1, 11, 45, true)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
                    return null;
                }
            } else {
                if (slotID >= 0 && slotID <= 9) {
                    if (!this.mergeItemStack(itemstack1, 11, 45, true)) {
                        return null;
                    }
                } else if (!this.mergeItemStack(itemstack1, 9, 10, false)) {
                    return null;
                }
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }
    
    // Merge method that obeys stack size limit and slot validity
    @Override
    protected boolean mergeItemStack(ItemStack itemStack, int slotMin, int slotMax, boolean reverse) {
        boolean returnValue = false;
        int i = slotMin;

        if (reverse) {
            i = slotMax - 1;
        }

        Slot slot;
        if (itemStack.isStackable()) {
            while (itemStack.stackSize > 0 && (!reverse && i < slotMax || reverse && i >= slotMin)) {
                slot = (Slot) this.inventorySlots.get(i);
                ItemStack slotStack = slot.getStack();

                if (slot.isItemValid(itemStack) && slotStack != null && slotStack.itemID == itemStack.itemID && (!itemStack.getHasSubtypes() || itemStack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
                    int total = slotStack.stackSize + itemStack.stackSize;
                    int max = Math.min(itemStack.getMaxStackSize(), slot.getSlotStackLimit());

                    if (total <= max) {
                        itemStack.stackSize = 0;
                        slotStack.stackSize = total;
                        slot.onSlotChanged();
                        returnValue = true;
                    } else if (slotStack.stackSize < max) {
                        itemStack.stackSize -= max - slotStack.stackSize;
                        slotStack.stackSize = max;
                        slot.onSlotChanged();
                        returnValue = true;
                    }
                }

                if (reverse) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (itemStack.stackSize > 0) {
            if (reverse) {
                i = slotMax - 1;
            } else {
                i = slotMin;
            }

            while (!reverse && i < slotMax || reverse && i >= slotMin) {
                slot = (Slot) this.inventorySlots.get(i);
                ItemStack slotStack = slot.getStack();

                if (slot.isItemValid(itemStack) && slotStack == null) {
                    int max = Math.min(itemStack.getMaxStackSize(), slot.getSlotStackLimit());
                    max = Math.min(itemStack.stackSize, max);
                    ItemStack copy = itemStack.copy();
                    copy.stackSize = max;
                    slot.putStack(copy);
                    slot.onSlotChanged();
                    itemStack.stackSize -= max;
                    return true;
                }

                if (reverse) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return returnValue;
    }
    
}
