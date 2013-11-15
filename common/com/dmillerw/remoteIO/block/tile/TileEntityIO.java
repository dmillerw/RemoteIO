package com.dmillerw.remoteIO.block.tile;

import java.util.Random;

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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cofh.api.energy.IEnergyHandler;

import com.dmillerw.remoteIO.client.fx.FXParticlePath;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.BlockState;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.ITrackerCallback;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.TrackedBlock;
import com.dmillerw.remoteIO.item.ItemGoggles;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

public class TileEntityIO extends TileEntityCore implements ITrackerCallback, IInventory, ISidedInventory, IFluidHandler, IPowerReceptor, IPowerEmitter, IEnergyHandler {

	public IInventory upgrades = new InventoryBasic("Upgrades", false, 9);
	public IInventory camo = new InventoryBasic("Camo", false, 1) {
		@Override
		public void onInventoryChanged() {
			super.onInventoryChanged();
			if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	};
	
	public boolean validCoordinates = false;
	public boolean creativeMode = false;
	public boolean redstoneState = false;

	public int x;
	public int y;
	public int z;
	public int d;
	
	@Override
	public void onBlockChanged(TrackedBlock tracked) {
		if (validCoordinates) {
			if (tracked.state == BlockState.REMOVED) {
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
		}
	}
	
	public void setCoordinates(int x, int y, int z, int d) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
		
		setValid(true);
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
		nbt.setBoolean("creative", this.creativeMode);
		
		if (this.validCoordinates) {
			NBTTagCompound coords = new NBTTagCompound();
			coords.setInteger("x", x);
			coords.setInteger("y", y); 
			coords.setInteger("z", z);
			coords.setInteger("d", d);
			nbt.setCompoundTag("coords", coords);
		}
		
		NBTTagCompound upgradesNBT = new NBTTagCompound();
		InventoryHelper.writeToNBT(upgrades, upgradesNBT);
		NBTTagCompound camoNBT = new NBTTagCompound();
		InventoryHelper.writeToNBT(camo, camoNBT);
		
		nbt.setCompoundTag("upgrades", upgradesNBT);
		nbt.setCompoundTag("camo", camoNBT);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.creativeMode = nbt.getBoolean("creative");
		
		if (nbt.hasKey("coords")) {
			NBTTagCompound coords = nbt.getCompoundTag("coords");
			this.x = coords.getInteger("x");
			this.y = coords.getInteger("y");
			this.z = coords.getInteger("z");
			this.d = coords.getInteger("d");
			setValid(true);
		} else {
			setValid(false);
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
	}
	
	public TileEntity getTileEntity() {
		if (!this.worldObj.isRemote) {
			if (hasUpgrade(Upgrade.REDSTONE) && redstoneState) {
				return null;
			}
			
			if (validCoordinates) {
				if (!hasUpgrade(Upgrade.CROSS_DIMENSIONAL) && this.worldObj.provider.dimensionId != this.d) {
					return null;
				} else {
					try {
						TileEntity tile = null;
						
						if (this.worldObj.provider.dimensionId == this.d) {
							if (this.inRange()) {
								tile = worldObj.getBlockTileEntity(x, y, z);
							}
						} else {
							World world = MinecraftServer.getServer().worldServerForDimension(d);
							tile = world.getBlockTileEntity(x, y, z);
						}

						if (tile != null) {
							return tile;
						} else {
							setValid(false);
							return null;
						}
					} catch(NullPointerException ex) {
						FMLLog.warning("[RemoteIO] The IO block at [" + xCoord + ", " + yCoord + ", " + zCoord + "] has an invalid dimension ID set. It will be reset!");
						this.clearCoordinates();
					}
				}
			}
		}

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
		if (getTileEntity() != null && getTileEntity() instanceof IPowerReceptor && hasUpgrade(Upgrade.POWER_BC)) {
			return (IPowerReceptor)getTileEntity();
		}
		
		return null;
	}
	
	private IPowerEmitter getBCPowerEmitter() {
		if (getTileEntity() != null && getTileEntity() instanceof IPowerEmitter && hasUpgrade(Upgrade.POWER_BC)) {
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
	
	public boolean hasUpgrade(Upgrade upgrade) {
		return InventoryHelper.inventoryContains(upgrades, upgrade.toItemStack(), false) || creativeMode;
	}

	private int upgradeCount(Upgrade upgrade) {
		return InventoryHelper.amountContained(upgrades, upgrade.toItemStack(), false);
	}
	
	private boolean inRange() {
		if (this.validCoordinates) {
			int maxRange = (upgradeCount(Upgrade.RANGE) * 8) + 8;
			int dX = Math.abs(this.xCoord - this.x);
			int dY = Math.abs(this.yCoord - this.y);
			int dZ = Math.abs(this.zCoord - this.z);

			return (dX <= maxRange || dY <= maxRange || dZ <= maxRange || this.creativeMode);
		}
		
		return false;
	}
	
	private void setValid(boolean valid) {
		this.validCoordinates = valid;
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (valid && !worldObj.isRemote) {
			BlockTracker.getInstance().track(getTileEntity(), this);
		}
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

}
