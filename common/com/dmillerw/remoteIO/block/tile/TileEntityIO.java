package com.dmillerw.remoteIO.block.tile;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;

import java.util.Random;

import universalelectricity.core.block.IElectrical;
import universalelectricity.core.block.IElectricalStorage;
import universalelectricity.core.electricity.ElectricityPack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.client.fx.FXParticlePath;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.BlockState;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.ITrackerCallback;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.TrackedBlock;
import com.dmillerw.remoteIO.item.ItemGoggles;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import cpw.mods.fml.client.FMLClientHandler;

public class TileEntityIO extends TileEntityCore implements ITrackerCallback, IInventory, ISidedInventory, IFluidHandler, IPowerReceptor, IPowerEmitter, IEnergyHandler, IEnergyStorage, IEnergySource, IEnergySink, IElectrical {

	public IInventory upgrades = new InventoryBasic("Upgrades", false, 9);
	public IInventory camo = new InventoryBasic("Camo", false, 1) {
		@Override
		public void onInventoryChanged() {
			super.onInventoryChanged();
			if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	};
	
	public boolean validCoordinates = false;
	public boolean redstoneState = false;

	public boolean addedToEnergyNet = false; // Requirement of IC2 :(
	
	public boolean firstLoad = true;
	
	public int x;
	public int y;
	public int z;
	public int d;
	
	@Override
	public void onBlockChanged(TrackedBlock tracked) {
		if (validCoordinates) {
			if (tracked.state == BlockState.REMOVED && !hasUpgrade(Upgrade.LINK_PERSIST)) {
				setValid(false);
			}
		} else {
			tracked.destroy();
		}
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (ItemGoggles.isPlayerWearing(FMLClientHandler.instance().getClient().thePlayer) && validCoordinates && worldObj.provider.dimensionId == this.d) {
				Random rand = new Random();
				for (int i=0; i<rand.nextInt(5); i++) {
					FXParticlePath path = new FXParticlePath(worldObj, this, x + 0.5F, y + 0.5F, z + 0.5F, 0.25F + (0.05F * rand.nextFloat()));
					path.setRBGColorF(0.35F, 0.35F, 1F);
					Minecraft.getMinecraft().effectRenderer.addEffect(path);
				}
			}
		} else {
			if (!addedToEnergyNet) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile)this));
				addedToEnergyNet = true;
			}
			
			if (firstLoad) {
				setValid(validCoordinates);
				firstLoad = false;
			}
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		
		if (!worldObj.isRemote) {
			BlockTracker.getInstance().removeAllWithCallback(this);
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
			addedToEnergyNet = false;
		}
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();

		if (!worldObj.isRemote) {
			BlockTracker.getInstance().removeAllWithCallback(this);
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
			addedToEnergyNet = false;
		}
	}
	
	public void onBlockBroken() {
		if (!worldObj.isRemote) {
			BlockTracker.getInstance().removeAllWithCallback(this);
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
			addedToEnergyNet = false;
		}
	}
	
	public boolean setCoordinates(int x, int y, int z, int d) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
		
		boolean valid = getTileEntity(true) != null;
		
		if (valid && !worldObj.isRemote) {
			BlockTracker.getInstance().track(worldObj.provider.dimensionId, x, y, z, this);
		}
		
		setValid(valid);
		
		return valid;
	}
	
	public boolean hasCoordinates() {
		return this.validCoordinates;
	}
	
	public void clearCoordinates() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.d = 0;
		
		setValid(false);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		if (this.validCoordinates) {
			NBTTagCompound coords = new NBTTagCompound();
			coords.setInteger("x", x);
			coords.setInteger("y", y); 
			coords.setInteger("z", z);
			coords.setInteger("d", d);
			nbt.setCompoundTag("coords", coords);
		}
		
		nbt.setBoolean("redstone", this.redstoneState);
		
		NBTTagCompound upgradesNBT = new NBTTagCompound();
		InventoryHelper.writeToNBT(upgrades, upgradesNBT);
		NBTTagCompound camoNBT = new NBTTagCompound();
		InventoryHelper.writeToNBT(camo, camoNBT);
		
		nbt.setCompoundTag("upgrades", upgradesNBT);
		nbt.setCompoundTag("camo", camoNBT);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("coords")) {
			NBTTagCompound coords = nbt.getCompoundTag("coords");
			this.x = coords.getInteger("x");
			this.y = coords.getInteger("y");
			this.z = coords.getInteger("z");
			this.d = coords.getInteger("d");
			this.validCoordinates = true;
		} else {
			this.validCoordinates = false;
		}
		
		if (nbt.hasKey("redstone")) {
			this.redstoneState = nbt.getBoolean("redstone");
		}
		
		if (nbt.hasKey("upgrades")) {
			ItemStack[] items = InventoryHelper.readFromNBT(upgrades, nbt.getCompoundTag("upgrades"));
			for (int i=0; i<items.length; i++) {
				this.upgrades.setInventorySlotContents(i, items[i]);
			}
		}
		
		if (nbt.hasKey("camo")) {
			ItemStack[] items = InventoryHelper.readFromNBT(camo, nbt.getCompoundTag("camo"));
			for (int i=0; i<items.length; i++) {
				this.camo.setInventorySlotContents(i, items[i]);
			}
		}
	}
	
	public void setRedstoneState(boolean state) {
		this.redstoneState = state;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public TileEntity getTileEntity() {
		return getTileEntity(false);
	}
	
	public TileEntity getTileEntity(boolean verify) {
		if (!this.worldObj.isRemote) {
			if (!validCoordinates && !verify) {
				return null;
			}
			
			if (hasUpgrade(Upgrade.REDSTONE) && redstoneState && !verify) {
				return null;
			}
			
			return (this.worldObj.provider.dimensionId == this.d) ? getTileEntityInDimension(verify) : getTileEntityOutDimension(verify);
		}

		return null;
	}
	
	private TileEntity getTileEntityInDimension(boolean verify) {
		if (inRange(verify)) {
			return this.worldObj.getBlockTileEntity(x, y, z);
		}
		
		return null;
	}
	
	private TileEntity getTileEntityOutDimension(boolean verify) {
//		if (hasUpgrade(Upgrade.CROSS_DIMENSIONAL) || verify) {
//			WorldServer world = MinecraftServer.getServer().worldServerForDimension(this.d);
//			return world.getBlockTileEntity(x, y, z);
//		}
		
		return null;
	}
	
	private IInventory getInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof IInventory && hasUpgrade(Upgrade.ITEM)) {
			return (IInventory)getTileEntity();
		}
		
		return null;
	}
	
	private ISidedInventory getSidedInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof ISidedInventory && hasUpgrade(Upgrade.ISIDED_AWARE)) {
			return (ISidedInventory)getTileEntity();
		}
		
		return null;
	}
	
	private IFluidHandler getFluidHandler() {
		if (getTileEntity() != null && getTileEntity() instanceof IFluidHandler && hasUpgrade(Upgrade.FLUID)) {
			return (IFluidHandler)getTileEntity();
		}
		
		return null;
	}
	
	private IPowerReceptor getBCPowerReceptor() {
		if (getTileEntity() != null && getTileEntity() instanceof IPowerReceptor && hasUpgrade(Upgrade.POWER_MJ)) {
			return (IPowerReceptor)getTileEntity();
		}
		
		return null;
	}
	
	private IPowerEmitter getBCPowerEmitter() {
		if (getTileEntity() != null && getTileEntity() instanceof IPowerEmitter && hasUpgrade(Upgrade.POWER_MJ)) {
			return (IPowerEmitter)getTileEntity();
		}
		
		return null;
	}	
	
	private IEnergyHandler getRFHandler() {
		if (getTileEntity() != null && getTileEntity() instanceof IEnergyHandler && hasUpgrade(Upgrade.POWER_RF)) {
			return (IEnergyHandler)getTileEntity();
		}
		
		return null;
	}
	
	private IEnergyStorage getRFSource() {
		if (getTileEntity() != null && getTileEntity() instanceof IEnergyStorage && hasUpgrade(Upgrade.POWER_RF)) {
			return (IEnergyStorage)getTileEntity();
		}
		
		return null;
	}
	
	private IEnergySource getEUSource() {
		if (getTileEntity() != null && getTileEntity() instanceof IEnergySource && hasUpgrade(Upgrade.POWER_EU)) {
			return (IEnergySource)getTileEntity();
		}
		
		return null;
	}
	
	private IEnergySink getEUSink() {
		if (getTileEntity() != null && getTileEntity() instanceof IEnergySink && hasUpgrade(Upgrade.POWER_EU)) {
			return (IEnergySink)getTileEntity();
		}
		
		return null;
	}
	
	private IElectrical getUEElectrical() {
		if (getTileEntity() != null && getTileEntity() instanceof IElectrical && hasUpgrade(Upgrade.POWER_UE)) {
			return (IElectrical)getTileEntity();
		}
		
		return null;
	}
	
	private IElectricalStorage getUEStorage() {
		if (getTileEntity() != null && getTileEntity() instanceof IElectricalStorage && hasUpgrade(Upgrade.POWER_UE)) {
			return (IElectricalStorage)getTileEntity();
		}
		
		return null;
	}
	
	public boolean hasUpgrade(Upgrade upgrade) {
		return InventoryHelper.inventoryContains(upgrades, upgrade.toItemStack(), false);
	}

	private int upgradeCount(Upgrade upgrade) {
		return InventoryHelper.amountContained(upgrades, upgrade.toItemStack(), false);
	}
	
	private boolean inRange(boolean verify) {
		if (this.validCoordinates || verify) {
			int maxRange = (upgradeCount(Upgrade.RANGE) * 8) + 8;
			int dX = Math.abs(this.xCoord - this.x);
			int dY = Math.abs(this.yCoord - this.y);
			int dZ = Math.abs(this.zCoord - this.z);

			return (dX <= maxRange && dY <= maxRange && dZ <= maxRange);
		}
		
		return false;
	}
	
	/** Purely for the visual side of things */
	private void setValid(boolean valid) {
		this.validCoordinates = valid;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, RemoteIO.instance.config.blockRIOID);
	}
	
	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return getInventory() != null ? getInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInventory() != null ? getInventory().getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInventory() != null ? getInventory().decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getInventory() != null ? getInventory().getStackInSlotOnClosing(i) : null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return getInventory() != null ? getInventory().getInvName() : null;
	}

	@Override
	public boolean isInvNameLocalized() {
		return getInventory() != null ? getInventory().isInvNameLocalized() : false;
	}

	@Override
	public int getInventoryStackLimit() {
		return getInventory() != null ? getInventory().getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openChest() {
		
	}

	@Override
	public void closeChest() {
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return getInventory() != null ? getInventory().isItemValidForSlot(i, itemstack) : false;
	}

	/* ISIDEDINVENTORY */
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		int[] defaultSlots = new int[getSizeInventory()];
		for (int i=0; i<defaultSlots.length; i++) {
			defaultSlots[i] = i;
		}
		return getSidedInventory() != null ? getSidedInventory().getAccessibleSlotsFromSide(var1) : defaultSlots;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return getSidedInventory() != null ? getSidedInventory().canInsertItem(i, itemstack, j) : true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return getSidedInventory() != null ? getSidedInventory().canExtractItem(i, itemstack, j) : true;
	}
	
	/* IFLUIDHANDLER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return getFluidHandler() != null ? getFluidHandler().fill(from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return getFluidHandler() != null ? getFluidHandler().drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return getFluidHandler() != null ? getFluidHandler().drain(from, maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return getFluidHandler() != null ? getFluidHandler().canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return getFluidHandler() != null ? getFluidHandler().canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return getFluidHandler() != null ? getFluidHandler().getTankInfo(from) : new FluidTankInfo[0];
	}

	/* IPOWEREMITTER */
	@Override
	public boolean canEmitPowerFrom(ForgeDirection side) {
		return getBCPowerEmitter() != null ? getBCPowerEmitter().canEmitPowerFrom(side) : false;
	}

	/* IPOWERRECEPTOR */
	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		return getBCPowerReceptor() != null ? getBCPowerReceptor().getPowerReceiver(side) : null;
	}

	@Override
	public void doWork(PowerHandler workProvider) {
		if (getBCPowerReceptor() != null) getBCPowerReceptor().doWork(workProvider);
	}

	@Override
	public World getWorld() {
		return getBCPowerReceptor() != null ? getBCPowerReceptor().getWorld() : null;
	}

	/* IENERGYHANDLER */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return getRFHandler() != null ? getRFHandler().receiveEnergy(from, maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return getRFHandler() != null ? getRFHandler().extractEnergy(from, maxExtract, simulate) : 0;
	}

	@Override
	public boolean canInterface(ForgeDirection from) {
		return getRFHandler() != null ? getRFHandler().canInterface(from) : false;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return getRFHandler() != null ? getRFHandler().getEnergyStored(from) : 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return getRFHandler() != null ? getRFHandler().getMaxEnergyStored(from) : 0;
	}

	/* IENERGYSTORAGE */
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return getRFSource() != null ? getRFSource().receiveEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return getRFSource() != null ? getRFSource().extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public int getEnergyStored() {
		return getRFSource() != null ? getRFSource().getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return getRFSource() != null ? getRFSource().getMaxEnergyStored() : 0;
	}
	
	/* IENERGYSOURCE */
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return getEUSource() != null ? getEUSource().emitsEnergyTo(receiver, direction) : false;
	}

	@Override
	public double getOfferedEnergy() {
		return getEUSource() != null ? getEUSource().getOfferedEnergy() : 0;
	}

	@Override
	public void drawEnergy(double amount) {
		if (getEUSource() != null) getEUSource().drawEnergy(amount);
	}

	/* IENERGYSINK */
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return getEUSink() != null ? getEUSink().acceptsEnergyFrom(emitter, direction) : false;
	}

	@Override
	public double demandedEnergyUnits() {
		return getEUSink() != null ? getEUSink().demandedEnergyUnits() : 0;
	}

	@Override
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
		return getEUSink() != null ? getEUSink().injectEnergyUnits(directionFrom, amount) : 0;
	}

	@Override
	public int getMaxSafeInput() {
		return getEUSink() != null ? getEUSink().getMaxSafeInput() : Integer.MAX_VALUE;
	}

	/* IELECTRICAL */
	@Override
	public boolean canConnect(ForgeDirection direction) {
		return getUEElectrical() != null ? getUEElectrical().canConnect(direction) : false;
	}

	@Override
	public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive) {
		return getUEElectrical() != null ? getUEElectrical().receiveElectricity(from, receive, doReceive) : 0;
	}

	@Override
	public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide) {
		return getUEElectrical() != null ? getUEElectrical().provideElectricity(from, request, doProvide) : null;
	}

	@Override
	public float getRequest(ForgeDirection direction) {
		return getUEElectrical() != null ? getUEElectrical().getRequest(direction) : 0;
	}

	@Override
	public float getProvide(ForgeDirection direction) {
		return getUEElectrical() != null ? getUEElectrical().getProvide(direction) : 0;
	}

	@Override
	public float getVoltage() {
		return getUEElectrical() != null ? getUEElectrical().getVoltage() : 0;
	}

}
