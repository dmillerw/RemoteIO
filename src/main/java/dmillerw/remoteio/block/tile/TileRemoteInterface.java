package dmillerw.remoteio.block.tile;

import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.IBatteryProvider;
import buildcraft.api.mj.MjAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.helper.MatrixHelper;
import dmillerw.remoteio.core.helper.RotationHelper;
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
import org.lwjgl.util.vector.Matrix4f;
import thaumcraft.api.aspects.*;
import thaumcraft.api.wands.IWandable;

/**
 * @author dmillerw
 */
public class TileRemoteInterface extends TileIOCore implements BlockTracker.ITrackerCallback, InventoryNBT.IInventoryCallback, IInventory, IFluidHandler, IAspectContainer, IAspectSource, IEssentiaTransport, IEnergySource, IEnergySink, IBatteryProvider, IWandable, IWrenchable {

	@Override
	public void callback(IBlockAccess world, int x, int y, int z) {
		updateVisualState();
		updateNeighbors();
	}

	@Override
	public void callback(IInventory inventory) {
		// Eww, hacky workarounds
		// Recalculates BC state to account for insertion/removal of BC transfer chip
		// Can't think of a better way to do this
		if (hasTransferChip(TransferType.ENERGY_BC)) {
			mjBatteryCache = MjAPI.getMjBattery(remotePosition.getTileEntity());
		}

		// I think IC2 caches tile state...
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		if (!registeredWithIC2 && hasTransferChip(TransferType.ENERGY_IC2) && remotePosition.getTileEntity() instanceof IEnergyTile) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			registeredWithIC2 = true;
		}

		// Clear missing upgrade flag
		missingUpgrade = false;

		updateVisualState();
		updateNeighbors();
	}

	@SideOnly(Side.CLIENT)
	public ItemStack simpleCamo;

	public VisualState visualState = VisualState.INACTIVE;

	public DimensionalCoords remotePosition;

	public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
	public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

	private IBatteryObject mjBatteryCache;

	// THIS IS NOT AN ANGLE, BUT THE NUMBER OF LEFT-HAND ROTATIONS!
	public int rotationY = 0;

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
		nbt.setInteger("axisY", this.rotationY);
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
		rotationY = nbt.getInteger("axisY");
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

		if (nbt.hasKey("axisY")) {
			rotationY = nbt.getInteger("axisY");
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
	public void updateRotation(int modification) {
		this.rotationY += modification;
		if (rotationY > 3) {
			rotationY = 0;
		} else if (rotationY < 0) {
			rotationY = 3;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("axisY", this.rotationY);
		sendClientUpdate(nbt);
	}

	/** Calculates the visual state based on various parameters */
	private VisualState calculateVisualState() {
		if (remotePosition == null) {
			return VisualState.INACTIVE;
		} else {
			if (remotePosition != null && !remotePosition.blockExists()) {
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

		if (!registeredWithIC2 && hasTransferChip(TransferType.ENERGY_IC2) && remotePosition.getTileEntity() instanceof IEnergyTile) {
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

		if (!remotePosition.blockExists()) {
			return null;
		}

		TileEntity remote = remotePosition.getTileEntity();

		if (remote != null) {
			if (!(cls.isInstance(remote))) {
				return null;
			}
		} else {
			if (!(cls.isInstance(remotePosition.getBlock()))) {
				return null;
			}
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

		if (!remotePosition.blockExists()) {
			return null;
		}

		TileEntity remote = remotePosition.getTileEntity();

		if (remote != null) {
			if (!(cls.isInstance(remote))) {
				return null;
			}
		} else {
			if (!(cls.isInstance(remotePosition.getBlock()))) {
				return null;
			}
		}

		if (requiresChip) {
			if (!hasTransferChip(TransferType.getTypeForInterface(cls))) {
				if (missingUpgrade = false) {
					missingUpgrade = true;
					updateVisualState();
				}
				return null;
			}
		}

		return cls.cast(remote);
	}

	public ForgeDirection getAdjustedSide(ForgeDirection side) {
		if (side == ForgeDirection.WEST || side == ForgeDirection.EAST) {
			side = side.getOpposite();
		}
		return ForgeDirection.getOrientation(RotationHelper.getRotatedSide(0, rotationY, 0, side.ordinal()));
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
		return fluidHandler != null ? fluidHandler.fill(getAdjustedSide(from), resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(getAdjustedSide(from), resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.drain(getAdjustedSide(from), maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canFill(getAdjustedSide(from), fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.canDrain(getAdjustedSide(from), fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		IFluidHandler fluidHandler = (IFluidHandler) getTransferImplementation(IFluidHandler.class);
		return fluidHandler != null ? fluidHandler.getTankInfo(getAdjustedSide(from)) : new FluidTankInfo[0];
	}

	/* IASPECTCONTAINER */
	@Override
	public AspectList getAspects() {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.getAspects() : new AspectList();
	}

	@Override
	public void setAspects(AspectList aspects) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		if (aspectContainer != null) aspectContainer.setAspects(aspects);
	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.doesContainerAccept(tag) : false;
	}

	@Override
	public int addToContainer(Aspect tag, int amount) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.addToContainer(tag, amount) : amount;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.takeFromContainer(tag, amount) : false;
	}

	@Override
	public boolean takeFromContainer(AspectList ot) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.takeFromContainer(ot) : false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.doesContainerContainAmount(tag, amount): false;
	}

	@Override
	public boolean doesContainerContain(AspectList ot) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.doesContainerContain(ot) : false;
	}

	@Override
	public int containerContains(Aspect tag) {
		IAspectContainer aspectContainer = (IAspectContainer) getTransferImplementation(IAspectContainer.class);
		return aspectContainer != null ? aspectContainer.containerContains(tag) : 0;
	}

	/* IESSENTIATRANSPORT */
	@Override
	public boolean isConnectable(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.isConnectable(getAdjustedSide(face)) : false;
	}

	@Override
	public boolean canInputFrom(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.canInputFrom(getAdjustedSide(face)) : false;
	}

	@Override
	public boolean canOutputTo(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.canOutputTo(getAdjustedSide(face)) : false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		if (essentiaTransport != null) essentiaTransport.setSuction(aspect, amount);
	}

	@Override
	public Aspect getSuctionType(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.getSuctionType(getAdjustedSide(face)) : null;
	}

	@Override
	public int getSuctionAmount(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.getSuctionAmount(getAdjustedSide(face)) : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.takeEssentia(aspect, amount, getAdjustedSide(face)) : 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.addEssentia(aspect, amount, getAdjustedSide(face)) : 0;
	}

	@Override
	public Aspect getEssentiaType(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.getEssentiaType(getAdjustedSide(face)) : null;
	}

	@Override
	public int getEssentiaAmount(ForgeDirection face) {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.getEssentiaAmount(getAdjustedSide(face)) : 0;
	}

	@Override
	public int getMinimumSuction() {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.getMinimumSuction() : 0;
	}

	@Override
	public boolean renderExtendedTube() {
		IEssentiaTransport essentiaTransport = (IEssentiaTransport) getTransferImplementation(IEssentiaTransport.class);
		return essentiaTransport != null ? essentiaTransport.renderExtendedTube() : false;
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
		return energySource != null ? energySource.emitsEnergyTo(receiver, getAdjustedSide(direction)) : false;
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
		return energySink != null ? energySink.acceptsEnergyFrom(emitter, getAdjustedSide(direction)) : false;
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
		return wandable != null ? wandable.onWandRightClick(world, wandstack, player, x, y, z, getAdjustedSide(ForgeDirection.getOrientation(side)).ordinal(), md) : -1;
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
