package dmillerw.remoteio.block.tile;

import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.IBatteryProvider;
import buildcraft.api.mj.MjAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.InventoryNBT;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.lib.DimensionalCoords;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thaumcraft.api.wands.IWandable;

/**
 * @author dmillerw
 */
public class TileRemoteInterface extends TileIOCore implements BlockTracker.ITrackerCallback, InventoryNBT.IInventoryCallback, IInventory, IFluidHandler, IEnergySource, IEnergySink, IBatteryProvider, IWandable, IWrenchable {

	@Override
	public void callback(IBlockAccess world, int x, int y, int z) {
		updateVisualState();
		markForUpdate();
	}

	@Override
	public void callback(IInventory inventory) {
		updateVisualState();

		// Eww, hacky workarounds
		// Recalculates BC state to account for insertion/removal of BC transfer chip
		// Can't think of a better way to do this
		if (hasTransferChip(TransferType.ENERGY_BC)) {
			mjBatteryCache = MjAPI.getMjBattery(remotePosition.getTileEntity());
			markForUpdate();
		}
	}

	@SideOnly(Side.CLIENT)
	public ItemStack simpleCamo;

	public VisualState visualState = VisualState.INACTIVE;

	public DimensionalCoords remotePosition;

	public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
	public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

	private IBatteryObject mjBatteryCache;

	public int thetaModifier = 0;

	public boolean camoRenderLock = false;

	private boolean registeredWithIC2 = false;
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
			if (visualState == VisualState.CAMOUFLAGE_REMOTE) {
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

		if (nbt.hasKey("simple")) {
			simpleCamo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("simple"));
		} else if (nbt.hasKey("simple_null")) {
			simpleCamo = null;
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
	public void onChunkUnload() {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		BlockTracker.INSTANCE.stopTracking(remotePosition);

		mjBatteryCache = null;
	}

	@Override
	public void invalidate() {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		BlockTracker.INSTANCE.stopTracking(remotePosition);

		mjBatteryCache = null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	/* BEGIN UPDATE METHODS */

	/** Scans for any simple camo chips and sends the contents of the first one it finds that has a stored block to the client */
	public void updateSimpleCamo() {
		ItemStack stack = null;
		for (ItemStack stack1 : InventoryHelper.toArray(upgradeChips)) {
			if (stack1 != null && stack1.getItemDamage() == UpgradeType.SIMPLE_CAMO) {
				stack = new InventoryItem(stack1, 1).getStackInSlot(0);
				if (stack != null) {
					break;
				}
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		if (stack != null) {
			NBTTagCompound tag = new NBTTagCompound();
			stack.writeToNBT(tag);
			nbt.setTag("simple", tag);
		} else {
			nbt.setBoolean("simple_null", true);
		}
		sendClientUpdate(nbt);
	}

	/** Sends the remote position to the client */
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

	/** Different from the other update methods, this calculates the visual state, then sends it to the client while also storing it in a local var */
	public void updateVisualState() {
		setVisualState(calculateVisualState());
	}

	/** Sends the passed in theta modifer to the client */
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

	/** Calculates the visual state based on various parameters */
	private VisualState calculateVisualState() {
		if (remotePosition == null) {
			return VisualState.INACTIVE;
		} else {
			if (remotePosition != null && remotePosition.getTileEntity() == null) {
				return VisualState.INACTIVE_BLINK;
			}

			boolean simple = hasUpgradeChip(UpgradeType.SIMPLE_CAMO);
			boolean remote = hasUpgradeChip(UpgradeType.REMOTE_CAMO);

			if (simple && !remote) {
				return VisualState.CAMOUFLAGE_SIMPLE;
			} else if (!simple && remote) {
				return VisualState.CAMOUFLAGE_REMOTE;
			} else if (simple && remote) {
				return VisualState.CAMOUFLAGE_BOTH;
			}

			return missingUpgrade ? VisualState.ACTIVE_BLINK : VisualState.ACTIVE;
		}
	}

	/** Sends the passed in visual state to the client, calling other update methods if applicable */
	private void setVisualState(VisualState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		sendClientUpdate(nbt);

		if (state == VisualState.CAMOUFLAGE_SIMPLE || state == VisualState.CAMOUFLAGE_BOTH) {
			updateSimpleCamo();
		}

		if (state == VisualState.CAMOUFLAGE_REMOTE || state == VisualState.CAMOUFLAGE_BOTH) {
			updateRemotePosition();
		}

		visualState = state;
	}

	/** Sets the server-side remote position to the passed in position, and resets the tracker */
	public void setRemotePosition(DimensionalCoords coords) {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		BlockTracker.INSTANCE.stopTracking(remotePosition);
		remotePosition = coords;
		BlockTracker.INSTANCE.startTracking(remotePosition, this);

		if (hasTransferChip(TransferType.ENERGY_BC)) {
			mjBatteryCache = MjAPI.getMjBattery(remotePosition.getTileEntity());
		}

		if (!registeredWithIC2 && remotePosition.getTileEntity() instanceof IEnergyTile) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			registeredWithIC2 = true;
		}

		markForUpdate();
	}

	/* END UPDATE METHODS */

	public Object getUpgradeImplementation(Class cls) {
		return getUpgradeImplementation(cls, -1);
	}

	public Object getUpgradeImplementation(Class cls, int upgradeType) {
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

		if (upgradeType != -1) {
			if (!hasUpgradeChip(upgradeType)) {
				return null;
			}
		}

		return cls.cast(remote);
	}

	public Object getTransferImplementation(Class cls) {
		return getTransferImplementation(cls, true);
	}

	public Object getTransferImplementation(Class cls, boolean requiresChip) {
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

		if (requiresChip) {
			if (!hasTransferChip(TransferType.getTypeForInterface(cls))) {
				missingUpgrade = true;
				updateVisualState();
				return null;
			} else {
				missingUpgrade = false;
				updateVisualState();
			}
		}

		return cls.cast(remote);
	}

	public boolean hasTransferChip(int type) {
		return InventoryHelper.containsStack(transferChips, new ItemStack(HandlerItem.transferChip, 1, type), true, false);
	}

	public boolean hasUpgradeChip(int type) {
		return InventoryHelper.containsStack(upgradeChips, new ItemStack(HandlerItem.upgradeChip, 1, type), true, false);
	}

	/* START IMPLEMENTATIONS */

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.getStackInSlot(slot) : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.decrStackSize(slot, amt) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.getStackInSlotOnClosing(slot) : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		if (inventory != null) inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.getInventoryName() : null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.hasCustomInventoryName() : false;
	}

	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
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
		IInventory inventory = (IInventory) getTransferImplementation(IInventory.class);
		return inventory != null ? inventory.isItemValidForSlot(slot, stack) : false;
	}

	/* IFLUIDHANDLER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.fill(from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(from, maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.getTankInfo(from) : new FluidTankInfo[0];
	}

	/* IENERGYSOURCE */
	@Override
	public double getOfferedEnergy() {
		IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
		return energySource != null ? energySource.getOfferedEnergy() : 0;
	}

	@Override
	public void drawEnergy(double amount) {
		IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
		if (energySource != null) energySource.drawEnergy(amount);
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		IEnergySource energySource = (IEnergySource) getTransferImplementation(IEnergySource.class);
		return energySource != null ? energySource.emitsEnergyTo(receiver, direction) : false;
	}

	/* IENERGYSINK */
	@Override
	public double demandedEnergyUnits() {
		IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
		return energySink != null ? energySink.demandedEnergyUnits() : 0;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
		return energySink != null ? energySink.injectEnergyUnits(directionFrom, amount) : 0;
	}

	@Override
	public int getMaxSafeInput() {
		IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
		return energySink != null ? energySink.getMaxSafeInput() : Integer.MAX_VALUE;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		IEnergySink energySink = (IEnergySink) getTransferImplementation(IEnergySink.class);
		return energySink != null ? energySink.acceptsEnergyFrom(emitter, direction) : false;
	}

	/* IBATTERYPROVIDER
	 * This is a funky implementation due to how the new MJ system works */
	@Override
	public IBatteryObject getMjBattery(String kind) {
		return mjBatteryCache;
	}

	/* IWANDABLE */
	@Override
	public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
		IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
		return wandable != null ? wandable.onWandRightClick(world, wandstack, player, x, y, z, side, md) : -1;
	}

	@Override
	public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
		IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
		return wandable != null ? wandable.onWandRightClick(world, wandstack, player) : wandstack;
	}

	@Override
	public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
		IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
		if (wandable != null) wandable.onUsingWandTick(wandstack, player, count);
	}

	@Override
	public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
		IWandable wandable = (IWandable) getUpgradeImplementation(IWandable.class, UpgradeType.REMOTE_ACCESS);
		if (wandable != null) wandable.onWandStoppedUsing(wandstack, world, player, count);
	}

	/* IWRENCHABLE */
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		IWrenchable wrenchable = (IWrenchable) getUpgradeImplementation(IWrenchable.class, UpgradeType.REMOTE_ACCESS);
		return wrenchable != null ? wrenchable.wrenchCanSetFacing(entityPlayer, side) : false;
 	}

	@Override
	public short getFacing() {
		IWrenchable wrenchable = (IWrenchable) getUpgradeImplementation(IWrenchable.class, UpgradeType.REMOTE_ACCESS);
		return wrenchable != null ? wrenchable.getFacing() : 0;
	}

	@Override
	public void setFacing(short facing) {
		IWrenchable wrenchable = (IWrenchable) getUpgradeImplementation(IWrenchable.class, UpgradeType.REMOTE_ACCESS);
		if (wrenchable != null) wrenchable.setFacing(facing);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public float getWrenchDropRate() {
		return 0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return null;
	}

	/* END IMPLEMENTATIONS */

	public static enum VisualState {
		INACTIVE,
		INACTIVE_BLINK,
		ACTIVE,
		ACTIVE_BLINK,
		CAMOUFLAGE_SIMPLE,
		CAMOUFLAGE_REMOTE,
		CAMOUFLAGE_BOTH;

		public boolean isCamouflage() {
			return this == CAMOUFLAGE_SIMPLE || this == CAMOUFLAGE_REMOTE || this == CAMOUFLAGE_BOTH;
		}
	}

}
