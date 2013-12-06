package com.dmillerw.remoteIO.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityHeater extends TileEntityCore {

	public TileEntity[] cachedFurnaces = new TileEntity[ForgeDirection.VALID_DIRECTIONS.length];
	
	public boolean hasLava = false;
	public boolean firstLoad = true;
	
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (firstLoad) {
				updateFurnaces();
				firstLoad = false;
			}
			
			if (this.worldObj.getTotalWorldTime() % 20 == 0) {
				updateLavaSources();
			}
			
			if (hasLava) {
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = this.cachedFurnaces[dir.ordinal()];
					
					if (tile != null && tile instanceof TileEntityFurnace) {
						((TileEntityFurnace)tile).currentItemBurnTime = ((TileEntityFurnace)tile).furnaceBurnTime = 100;
						
						if (this.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord) != Block.furnaceBurning.blockID) {
							BlockFurnace.updateFurnaceBlockState(true, worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
						}
					}
				}
			}
		}
	}
	
	public void onNeighborBlockUpdate() {
		updateLavaSources();
		updateFurnaces();
	}

	private void updateLavaSources() {
		int lavaSources = 0;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int id = this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			int meta = this.worldObj.getBlockMetadata(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (id == Block.lavaStill.blockID || (id == Block.lavaMoving.blockID && meta == 0)) {
				++lavaSources;
			}
		}
		if (lavaSources >= 2) {
			hasLava = true;
		} else {
			hasLava = false;
		}
		
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	private void updateFurnaces() {
		this.cachedFurnaces = new TileEntity[ForgeDirection.VALID_DIRECTIONS.length];
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int id = this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			
			if (id == Block.furnaceBurning.blockID || id == Block.furnaceIdle.blockID) {
				this.cachedFurnaces[dir.ordinal()] = this.worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			}
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		nbt.setBoolean("hasLava", this.hasLava);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.hasLava = nbt.getBoolean("hasLava");
	}
	
}
