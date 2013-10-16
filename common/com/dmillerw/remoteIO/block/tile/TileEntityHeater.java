package com.dmillerw.remoteIO.block.tile;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityHeater extends TileEntityCore {

	public boolean hasLava = false;
	
	public TileEntityFurnace furnaceTile = null;
	
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (this.worldObj.getTotalWorldTime() % 20 == 0) {
				onNeighborBlockUpdate(0);
			}
			
			if (this.worldObj.getTotalWorldTime() % 5 == 0) {
				if (furnaceTile != null && hasLava) {
					furnaceTile.furnaceBurnTime = furnaceTile.currentItemBurnTime = 20;
				}
			}
		}
	}
	
	public void onNeighborBlockUpdate(int blockID) {
		boolean prevHasLava = hasLava;
		int lavaSources = 0;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) == Block.lavaStill.blockID) {
				++lavaSources;
			}
		}
		if (lavaSources >= 2) {
			hasLava = true;
		} else {
			hasLava = false;
		}
		
		if (prevHasLava != hasLava) {
			updateLava();
		}
		
		
		if (blockID == Block.furnaceIdle.blockID || blockID == Block.furnaceBurning.blockID) {
			this.furnaceTile = null;
			
			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				int id = this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
				if (id == Block.furnaceIdle.blockID || id == Block.furnaceBurning.blockID) {
					this.furnaceTile = (TileEntityFurnace) worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
					break;
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setBoolean("hasLava", this.hasLava);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		this.hasLava = nbt.getBoolean("hasLava");
		this.updateLava();
	}
	
	@Override
	public void onUpdatePacket(NBTTagCompound tag) {
		if (tag.hasKey("hasLava")) {
			this.hasLava = tag.getBoolean("hasLava");
		} else {
			this.hasLava = false;
		}
		
		this.worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	private void updateLava() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("hasLava", this.hasLava);
		this.sendUpdateToClient(tag);
	}
	
}
