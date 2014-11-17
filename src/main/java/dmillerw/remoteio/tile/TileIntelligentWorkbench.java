package dmillerw.remoteio.tile;

import dmillerw.remoteio.inventory.InventoryTileCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

/**
 * @author dmillerw
 */
public class TileIntelligentWorkbench extends TileEntity {

    public InventoryTileCrafting craftMatrix = new InventoryTileCrafting(3, 3);

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            if (this.craftMatrix.getStackInSlot(i) != null) {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                this.craftMatrix.getStackInSlot(i).writeToNBT(data);
                nbtTagList.appendTag(data);
            }
        }
        nbtTagCompound.setTag("Items", nbtTagList);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        NBTTagList nbtTagList = nbtTagCompound.getTagList("Items", 10);
        this.craftMatrix = new InventoryTileCrafting(3, 3);

        for (int i = 0; i < nbtTagList.tagCount(); ++i) {
            NBTTagCompound data = nbtTagList.getCompoundTagAt(i);
            byte b0 = data.getByte("Slot");
            if (b0 >= 0 && b0 < this.craftMatrix.getSizeInventory()) {
                this.craftMatrix.setInventorySlotContents(b0, ItemStack.loadItemStackFromNBT(data));
            }
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}
