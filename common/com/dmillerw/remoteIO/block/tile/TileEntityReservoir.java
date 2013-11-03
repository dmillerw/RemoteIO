package com.dmillerw.remoteIO.block.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityReservoir extends TileEntityCore implements IFluidHandler {

	public boolean hasWater = false;
	
	public FluidTank waterTank = new FluidTank(FluidRegistry.WATER, 0, FluidContainerRegistry.BUCKET_VOLUME * 10);
	
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (this.worldObj.getTotalWorldTime() % 20 == 0) {
				updateWaterSources();
			}
			
			if (this.worldObj.getTotalWorldTime() % 5 == 0) {
				if (this.waterTank.getFluidAmount() + 250 <= this.waterTank.getCapacity()) {
					this.waterTank.fill(new FluidStack(FluidRegistry.WATER, 250), true);
				}
			}
		}
	}
	
	public void onNeighborBlockUpdate() {
		updateWaterSources();
	}

	private void updateWaterSources() {
		int waterSources = 0;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int id = this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			int meta = this.worldObj.getBlockMetadata(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (id == Block.waterStill.blockID || (id == Block.waterMoving.blockID && meta == 0)) {
				++waterSources;
			}
		}
		if (waterSources >= 2) {
			hasWater = true;
		} else {
			hasWater = false;
		}
		updateWater();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setBoolean("hasWater", this.hasWater);
		this.waterTank.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		this.hasWater = nbt.getBoolean("hasWater");
		this.waterTank = new FluidTank(FluidRegistry.WATER, 0, FluidContainerRegistry.BUCKET_VOLUME * 10);
		this.waterTank.readFromNBT(nbt);
	}
	
	@Override
	public void onUpdatePacket(NBTTagCompound tag) {
		if (tag.hasKey("hasWater")) {
			this.hasWater = tag.getBoolean("hasWater");
		} else {
			this.hasWater = false;
		}
		
		this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	private void updateWater() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("hasWater", this.hasWater);
		this.sendUpdateToClient(tag);
	}

	/* IFLUIDHANDLER */
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.waterTank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.waterTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return this.waterTank.getFluidAmount() > 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {this.waterTank.getInfo()};
	}
	
}
