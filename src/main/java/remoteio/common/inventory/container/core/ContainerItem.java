package remoteio.common.inventory.container.core;

import remoteio.common.inventory.InventoryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ContainerItem extends Container {

    protected InventoryItem inventory;

    private int activeSlot = -1;

    public ContainerItem(InventoryItem inventory, int activeSlot) {
        this.inventory = inventory;
        this.activeSlot = activeSlot;
    }

    @Override
    public ItemStack slotClick(int id, int x, int y, EntityPlayer player) {
        if (id == activeSlot) return null;
        return super.slotClick(id, x, y, player);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int id) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(id);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (id < 9) {
                if (!this.mergeItemStack(itemstack1, inventory.getSizeInventory(), 36 + inventory.getSizeInventory(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, inventory.getSizeInventory(), false)) {
                return null;
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

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        inventory.writeToNBT();

        player.setCurrentItemOrArmor(0, inventory.getStack());
        player.inventory.markDirty();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}