package com.dmillerw.remoteIO.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntitySideProxy extends TileEntityCore implements ISidedInventory, IFluidHandler {

	public ForgeDirection orientation = ForgeDirection.UNKNOWN;
	public ForgeDirection insertionSide = ForgeDirection.UNKNOWN;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		nbt.setByte("orientation", (byte) this.orientation.ordinal());
		nbt.setByte("insertionSide", (byte) this.insertionSide.ordinal());
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.orientation = ForgeDirection.values()[nbt.getByte("orientation")];
		this.insertionSide = ForgeDirection.values()[nbt.getByte("insertionSide")];
	}

	public boolean fullyValid() {
		return this.orientation != ForgeDirection.UNKNOWN && this.insertionSide != ForgeDirection.UNKNOWN;
	}
	
	public TileEntity getTileEntity() {
		return fullyValid() ? this.worldObj.getBlockTileEntity(xCoord + this.orientation.offsetX, yCoord + this.orientation.offsetY, zCoord + this.orientation.offsetZ) : null;
	}
	
	private IInventory getIInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof IInventory) {
			return (IInventory) getTileEntity();
		}
		return null;
	}
	
	private ISidedInventory getISidedInventory() {
		if (getTileEntity() != null && getTileEntity() instanceof ISidedInventory) {
			return (ISidedInventory) getTileEntity();
		}
		return null;
	}
	
	private IFluidHandler getIFluidHandler() {
		if (getTileEntity() != null && getTileEntity() instanceof IFluidHandler) {
			return (IFluidHandler) getTileEntity();
		}
		return null;
	}
	
	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return getIInventory() != null ? getIInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getIInventory() != null ? getIInventory().getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getIInventory() != null ? getIInventory().decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getIInventory() != null ? getIInventory().getStackInSlotOnClosing(i) : null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getIInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return getIInventory() != null ? getIInventory().getInvName() : null;
	}

	@Override
	public boolean isInvNameLocalized() {
		return getIInventory() != null ? getIInventory().isInvNameLocalized() : false;
	}

	@Override
	public int getInventoryStackLimit() {
		return getIInventory() != null ? getIInventory().getInventoryStackLimit() : 0;
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
		return getIInventory() != null ? getIInventory().isItemValidForSlot(i, itemstack) : false;
	}

	/* ISIDEDINVENTORY */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] defaultSlots = new int[getSizeInventory()];
		for (int i=0; i<getSizeInventory(); i++) {
			defaultSlots[i] = i;
		}
		return getISidedInventory() != null ? getISidedInventory().getAccessibleSlotsFromSide(this.insertionSide.ordinal()) : defaultSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return getISidedInventory() != null ? getISidedInventory().canInsertItem(slot, stack, this.insertionSide.ordinal()) : true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return getISidedInventory() != null ? getISidedInventory().canExtractItem(slot, stack, this.insertionSide.ordinal()) : true;
	}
	
	/* IFLUIDHANDLER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return getIFluidHandler() != null ? getIFluidHandler().fill(from, resource, doFill) : 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return getIFluidHandler() != null ? getIFluidHandler().drain(from, resource, doDrain) : null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return getIFluidHandler() != null ? getIFluidHandler().drain(from, maxDrain, doDrain) : null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return getIFluidHandler() != null ? getIFluidHandler().canFill(from, fluid) : false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return getIFluidHandler() != null ? getIFluidHandler().canDrain(from, fluid) : false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return getIFluidHandler() != null ? getIFluidHandler().getTankInfo(from) : new FluidTankInfo[0];
	}
	
}