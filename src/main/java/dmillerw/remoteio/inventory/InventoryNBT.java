package dmillerw.remoteio.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author dmillerw
 */
public class InventoryNBT extends InventoryBasic {

	private int maxStackSize = 64;

	public InventoryNBT(int size) {
		super("", false, size);
	}

	public InventoryNBT(int size, int maxStackSize) {
		super("", false, size);

		this.maxStackSize = maxStackSize;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagList items = nbt.getTagList("Items", 10);
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			byte b0 = item.getByte("Slot");

			if (b0 >= 0 && b0 < this.getSizeInventory()) {
				this.setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(item));
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (this.getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setByte("Slot", (byte) i);
				this.getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		nbt.setTag("Items", items);
	}

	@Override
	public int getInventoryStackLimit() {
		return maxStackSize;
	}
}
