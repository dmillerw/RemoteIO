package dmillerw.remoteio.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author dmillerw
 */
public class InventoryNBT extends InventoryBasic {

    private final IInventoryCallback callback;

    private int maxStackSize = 64;

    public InventoryNBT(IInventoryCallback callback, int size) {
        super("", false, size);

        this.callback = callback;
    }

    public InventoryNBT(IInventoryCallback callback, int size, int maxStackSize) {
        super("", false, size);

        this.callback = callback;
        this.maxStackSize = maxStackSize;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        callback.callback(this);
    }

    public void readFromNBT(String tag, NBTTagCompound nbt) {
        NBTTagList items = nbt.getTagList(tag, 10);
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte b0 = item.getByte("Slot");

            if (b0 >= 0 && b0 < this.getSizeInventory()) {
                this.setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(item));
            }
        }
    }

    public void writeToNBT(String tag, NBTTagCompound nbt) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                this.getStackInSlot(i).writeToNBT(item);
                items.appendTag(item);
            }
        }
        nbt.setTag(tag, items);
    }

    @Override
    public int getInventoryStackLimit() {
        return maxStackSize;
    }

    public static interface IInventoryCallback {
        public void callback(IInventory inventory);
    }
}
