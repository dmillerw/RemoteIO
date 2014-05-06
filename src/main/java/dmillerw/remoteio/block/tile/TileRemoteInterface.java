package dmillerw.remoteio.block.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

/**
 * @author dmillerw
 */
public class TileRemoteInterface extends TileIOCore implements BlockTracker.ITrackerCallback, IInventory {

	@Override
	public void callback(IBlockAccess world, int x, int y, int z) {
		setVisualState(calculateVisualState());
	}

	@SideOnly(Side.CLIENT)
	public VisualState visualState = VisualState.INACTIVE;

	public DimensionalCoords remotePosition;

	public boolean tempUseCamo = false;
	private boolean visualDirty = true;
	private boolean tracking = false;

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		if (remotePosition != null) {
			NBTTagCompound tag = new NBTTagCompound();
			remotePosition.writeToNBT(tag);
			nbt.setTag("position", tag);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("position")) {
			remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
		} else {
			tracking = true;
		}
	}

	@Override
	public void onClientUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("state")) {
			visualState = VisualState.values()[nbt.getByte("state")];
		}
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (visualDirty) {
				setVisualState(calculateVisualState());
				visualDirty = false;
			}

			if (!tracking) {
				BlockTracker.INSTANCE.startTracking(remotePosition, this);
				tracking = true;
			}
		}
	}

	public void forceVisualUpdate() {
		visualDirty = true;
	}

	public VisualState calculateVisualState() {
		if (remotePosition == null) {
			return VisualState.INACTIVE;
		} else {
			if (remotePosition != null && remotePosition.getTileEntity() == null) {
				return VisualState.INACTIVE_BLINK;
			}

			return tempUseCamo ? VisualState.REMOTE_CAMO : VisualState.ACTIVE;
		}
	}

	public void setVisualState(VisualState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		sendClientUpdate(nbt);
	}

	public void setRemotePosition(DimensionalCoords coords) {
		BlockTracker.INSTANCE.stopTracking(remotePosition);
		remotePosition = coords;
		visualDirty = true;
		BlockTracker.INSTANCE.startTracking(remotePosition, this);
	}

	public Object getImplementation(Class cls) {
		if (remotePosition == null) {
			return null;
		}

		TileEntity remote = remotePosition.getTileEntity();

		if (remote == null) {
			return null;
		}

		if (!(cls.isInstance(remote))) {
			return null;
		}

		return cls.cast(remote);
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.getStackInSlot(slot) : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.decrStackSize(slot, amt) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.getStackInSlotOnClosing(slot) : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		if (inventory != null) inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.getInventoryName() : null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.hasCustomInventoryName() : false;
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.isUseableByPlayer(player) : false;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = (IInventory) getImplementation(IInventory.class);
		return inventory != null ? inventory.isItemValidForSlot(slot, stack) : false;
	}

	public static enum VisualState {
		INACTIVE,
		INACTIVE_BLINK,
		ACTIVE,
		ACTIVE_BLINK,
		REMOTE_CAMO;
	}

}
