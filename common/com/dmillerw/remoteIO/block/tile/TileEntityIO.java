package com.dmillerw.remoteIO.block.tile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import com.dmillerw.remoteIO.item.Upgrade;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.IActionProvider;
import buildcraft.api.gates.IActionReceptor;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.transport.IPipe;
import buildcraft.core.IMachine;

public class TileEntityIO extends TileEntityCore implements IInventory, ISidedInventory, IFluidHandler, IMachine, IPowerReceptor, IPowerEmitter {

	public EnumSet<Upgrade> installedUpgrades = EnumSet.noneOf(Upgrade.class);
	
	public boolean validCoordinates = false;
	
	public int x;
	public int y;
	public int z;
	public int d;
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (worldObj.getTotalWorldTime() % 200 == 0) { // Force detection check every 10 seconds
				setValid(getTileEntity() != null);
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		if (this.validCoordinates) {
			NBTTagCompound coords = new NBTTagCompound();
			coords.setInteger("x", x);
			coords.setInteger("y", y);
			coords.setInteger("z", z);
			coords.setInteger("d", d);
			nbt.setCompoundTag("coords", coords);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		if (nbt.hasKey("coords")) {
			NBTTagCompound coords = nbt.getCompoundTag("coords");
			this.x = coords.getInteger("x");
			this.y = coords.getInteger("y");
			this.z = coords.getInteger("z");
			this.d = coords.getInteger("d");
			this.validCoordinates = true;
		}
	}
	
	public EnumSet<Interface> getValidInterfaces() {
		EnumSet set = EnumSet.noneOf(Interface.class);
		if (getInventory() != null) set.add(Interface.INVENTORY);
		if (getFluidHandler() != null) set.add(Interface.FLUID);
		return set;
	}
	
	public TileEntity getTileEntity() {
		if (!this.installedUpgrades.contains(Upgrade.CROSS_DIMENSIONAL) && this.worldObj.provider.dimensionId != this.d) {
			return null;
		} else {
			World world = MinecraftServer.getServer().worldServerForDimension(d);
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			if (tile != null) {
				return tile;
			} else {
				setValid(false);
				return null;
			}
		}
	}
	
	private IInventory getInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof IInventory && this.installedUpgrades.contains(Upgrade.ITEM)) {
			return (IInventory)getTileEntity();
		}
		
		return null;
	}
	
	private ISidedInventory getSidedInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof ISidedInventory && this.installedUpgrades.contains(Upgrade.ISIDED_AWARE)) {
			return (ISidedInventory)getTileEntity();
		}
		
		return null;
	}
	
	private IFluidHandler getFluidHandler() {
		if (getTileEntity() != null && getTileEntity() instanceof IFluidHandler && this.installedUpgrades.contains(Upgrade.FLUID)) {
			return (IFluidHandler)getTileEntity();
		}
		
		return null;
	}
	
	private IMachine getBCMachine() {
		if (getTileEntity() != null && getTileEntity() instanceof IMachine && this.installedUpgrades.contains(Upgrade.POWER_BC)) {
			return (IMachine)getTileEntity();
		}
		
		return null;
	}
	
	private IPowerReceptor getBCPowerReceptor() {
		if (getTileEntity() != null && getTileEntity() instanceof IPowerReceptor && this.installedUpgrades.contains(Upgrade.POWER_BC)) {
			return (IPowerReceptor)getTileEntity();
		}
		
		return null;
	}
	
	private IPowerEmitter getBCPowerEmitter() {
		if (getTileEntity() != null && getTileEntity() instanceof IPowerEmitter && this.installedUpgrades.contains(Upgrade.POWER_BC)) {
			return (IPowerEmitter)getTileEntity();
		}
		
		return null;
	}	
	
	private void setValid(boolean valid) {
		this.validCoordinates = valid;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("valid", valid);
		sendUpdateToClient(nbt);
	}
	
	@Override
	public void onUpdatePacket(NBTTagCompound tag) {
		if (tag.hasKey("valid")) {
			this.validCoordinates = tag.getBoolean("valid");
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

	/* IMACHINE */
	@Override
	public boolean isActive() {
		return getBCMachine() != null ? getBCMachine().isActive() : false;
	}

	@Override
	public boolean manageFluids() {
		return getBCMachine() != null ? getBCMachine().manageFluids() : false;
	}

	@Override
	public boolean manageSolids() {
		return getBCMachine() != null ? getBCMachine().manageSolids() : false;
	}

	@Override
	public boolean allowAction(IAction action) {
		return getBCMachine() != null ? getBCMachine().allowAction(action) : false;
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

	public enum Interface {
		INVENTORY, FLUID;
	}

}
