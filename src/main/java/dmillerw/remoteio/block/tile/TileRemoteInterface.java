package dmillerw.remoteio.block.tile;

import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.inventory.InventoryNBT;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * @author dmillerw
 */
public class TileRemoteInterface extends TileIOCore implements BlockTracker.ITrackerCallback, InventoryNBT.IInventoryCallback, IInventory, IFluidHandler {

	@Override
	public void callback(IBlockAccess world, int x, int y, int z) {
		updateVisualState();
	}

	@Override
	public void callback(IInventory inventory) {
		updateVisualState();
	}

	public VisualState visualState = VisualState.INACTIVE;

	public DimensionalCoords remotePosition;

	public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
	public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

	public int thetaModifier = 0;

	public boolean camoRenderLock = false;

	private boolean missingUpgrade = false;
	private boolean tracking = false;

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		transferChips.writeToNBT("TransferItems", nbt);
		upgradeChips.writeToNBT("UpgradeItems", nbt);

		if (remotePosition != null) {
			NBTTagCompound tag = new NBTTagCompound();
			remotePosition.writeToNBT(tag);
			nbt.setTag("position", tag);
		}

		// This is purely to ensure the client remains synchronized upon world load
		nbt.setByte("state", (byte) visualState.ordinal());
		nbt.setInteger("theta", thetaModifier);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		transferChips.readFromNBT("TransferItems", nbt);
		upgradeChips.readFromNBT("UpgradeItems", nbt);

		if (nbt.hasKey("position")) {
			remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
		} else {
			tracking = true;
		}

		// This is purely to ensure the client remains synchronized upon world load
		visualState = VisualState.values()[nbt.getByte("state")];
		thetaModifier = nbt.getInteger("theta");
	}

	@Override
	public void onClientUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("state")) {
			visualState = VisualState.values()[nbt.getByte("state")];

			if (visualState == VisualState.REMOTE_CAMO) {
				camoRenderLock = false;
			}
		}

		if (nbt.hasKey("position")) {
			remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
		} else if (nbt.hasKey("position_null")) {
			remotePosition = null;
		}

		if (nbt.hasKey("theta")) {
			thetaModifier = nbt.getInteger("theta");
		}
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (!tracking) {
				BlockTracker.INSTANCE.startTracking(remotePosition, this);
				tracking = true;
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void updateRemotePosition() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (remotePosition != null) {
			NBTTagCompound tag = new NBTTagCompound();
			remotePosition.writeToNBT(tag);
			nbt.setTag("position", tag);
		} else {
			nbt.setBoolean("position_null", true);
		}
		sendClientUpdate(nbt);
	}

	public void updateVisualState() {
		setVisualState(calculateVisualState());
	}

	public void updateThetaModifier(float thetaModifier) {
		this.thetaModifier += thetaModifier;

		if (this.thetaModifier < 0) {
			this.thetaModifier = 270;
		} else if (this.thetaModifier > 270) {
			this.thetaModifier = 0;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("theta", this.thetaModifier);
		sendClientUpdate(nbt);
	}

	private VisualState calculateVisualState() {
		if (remotePosition == null) {
			return VisualState.INACTIVE;
		} else {
			if (remotePosition != null && remotePosition.getTileEntity() == null) {
				return VisualState.INACTIVE_BLINK;
			}

			return hasUpgradeChip(UpgradeType.REMOTE_CAMO) ? VisualState.REMOTE_CAMO : missingUpgrade ? VisualState.ACTIVE_BLINK : VisualState.ACTIVE;
		}
	}

	private void setVisualState(VisualState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		sendClientUpdate(nbt);

		if (state == VisualState.REMOTE_CAMO) {
			updateRemotePosition();
		}

		visualState = state;
	}

	public void setRemotePosition(DimensionalCoords coords) {
		BlockTracker.INSTANCE.stopTracking(remotePosition);
		remotePosition = coords;
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

		if (!hasTransferChip(TransferType.getTypeForInterface(cls))) {
			missingUpgrade = true;
			updateVisualState();
			return null;
		} else {
			missingUpgrade = false;
			updateVisualState();
		}

		return cls.cast(remote);
	}

	public boolean hasTransferChip(int type) {
		return InventoryHelper.containsStack(transferChips, new ItemStack(HandlerItem.transferChip, 1, type), true, false);
	}

	public boolean hasUpgradeChip(int type) {
		return InventoryHelper.containsStack(upgradeChips, new ItemStack(HandlerItem.upgradeChip, 1, type), true, false);
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

	/* IFLUIDHANDLER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.fill(from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(from, maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidHandler fluidHandler = (IFluidHandler) getImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.getTankInfo(from) : new FluidTankInfo[0];
	}

	public static enum VisualState {
		INACTIVE,
		INACTIVE_BLINK,
		ACTIVE,
		ACTIVE_BLINK,
		REMOTE_CAMO;
	}

}
