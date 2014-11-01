package dmillerw.remoteio.tile.core;

import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.InventoryNBT;
import dmillerw.remoteio.lib.ModItems;
import dmillerw.remoteio.lib.VisualState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public abstract class TileIOCore extends TileCore implements InventoryNBT.IInventoryCallback {

    public VisualState visualState = VisualState.INACTIVE;

    public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
    public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        transferChips.writeToNBT("TransferChips", nbt);
        upgradeChips.writeToNBT("UpgradeChips", nbt);

        nbt.setByte("state", (byte) visualState.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        transferChips.readFromNBT("TransferChips", nbt);
        upgradeChips.readFromNBT("UpgradeChips", nbt);

        visualState = VisualState.values()[nbt.getByte("state")];
    }

    @Override
    public void onClientUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey("state")) {
            visualState = VisualState.values()[nbt.getByte("state")];
        }
    }

    public boolean hasTransferChip(int type) {
        return InventoryHelper.containsStack(transferChips, new ItemStack(ModItems.transferChip, 1, type), true, false);
    }

    public boolean hasUpgradeChip(int type) {
        return InventoryHelper.containsStack(upgradeChips, new ItemStack(ModItems.upgradeChip, 1, type), true, false);
    }

	/* BEGIN CLIENT UPDATE METHODS
     * 'update' methods are used to calculate what should be sent to the client
	 * 'send' methods actually send the data to the client, and take a single parameter
	 *  that is the data to be sent
	 *
	 *  Methods pertaining to the same data are lumped together */

    public abstract VisualState calculateVisualState();

    /**
     * This method kind of ignores the rules set above, as different blocks have different rules regarding visual state
     * so it calls an abstract method to determine the state, then updates as normal
     */
    public void updateVisualState() {
        VisualState state = calculateVisualState();
        this.visualState = state;
        sendVisualState(state);
    }

    public void sendVisualState(VisualState visualState) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("state", (byte) visualState.ordinal());
        sendClientUpdate(nbt);
    }

	/* END CLIENT UPDATE METHODS */
}
