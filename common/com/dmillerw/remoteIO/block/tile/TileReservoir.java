package com.dmillerw.remoteIO.block.tile;

import com.dmillerw.remoteIO.core.helper.StackHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileReservoir extends TileCore implements IFluidHandler {

	public boolean hasWater = false;
	
	public FluidTank waterTank = new FluidTank(FluidRegistry.WATER, 0, FluidContainerRegistry.BUCKET_VOLUME * 10);
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		ItemStack held = player.getHeldItem();
		
		if (!player.isSneaking() && held != null && !worldObj.isRemote) {
			FluidStack contained = this.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
			
			if (contained != null) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(contained, held);
				FluidStack toFill = FluidContainerRegistry.getFluidForFilledItem(filled);
				
				if (toFill != null) {
					if (held.stackSize > 1) {
						if (!player.inventory.addItemStackToInventory(filled)) {
							return false;
						} else {
							if (!player.capabilities.isCreativeMode) {
								if (--held.stackSize <= 0) {
									player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
								}
							}
						}
					} else {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, StackHelper.consumeItem(held));
						player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
					}
					
					this.drain(ForgeDirection.UNKNOWN, toFill, true);
				}
			}
			
			return false;
		}
		
		return true;
	}
	
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
		
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		nbt.setBoolean("hasWater", this.hasWater);
		this.waterTank.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.hasWater = nbt.getBoolean("hasWater");
		this.waterTank = new FluidTank(FluidRegistry.WATER, 0, FluidContainerRegistry.BUCKET_VOLUME * 10);
		this.waterTank.readFromNBT(nbt);
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