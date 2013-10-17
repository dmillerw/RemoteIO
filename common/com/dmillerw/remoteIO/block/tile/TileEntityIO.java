package com.dmillerw.remoteIO.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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

public class TileEntityIO extends TileEntityCore implements IInventory, IFluidHandler {

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
	
	private TileEntity getTileEntity() {
		World world = MinecraftServer.getServer().worldServerForDimension(d);
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		
		if (tile != null) {
			return tile;
		} else {
			setValid(false);
			return null;
		}
	}
	
	private IInventory getInventory() {
		if (getTileEntity() instanceof IInventory) {
			return (IInventory)getTileEntity();
		}
		
		return null;
	}
	
	private IFluidHandler getFluidHandler() {
		if (getTileEntity() instanceof IFluidHandler) {
			return (IFluidHandler)getTileEntity();
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

}
