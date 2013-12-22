package com.dmillerw.remoteIO.block.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileHeater extends TileCore {

	public boolean hasLava = false;
	public boolean firstLoad = true;
	
	public List<TileEntityFurnace> furnaceTiles = new ArrayList<TileEntityFurnace>();
	
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (firstLoad) {
				updateFurnaces();
				firstLoad = false;
			}
			
			if (this.worldObj.getTotalWorldTime() % 20 == 0) {
				updateLavaSources();
			}
			
			Iterator<TileEntityFurnace> iterator = furnaceTiles.iterator();
			while (iterator.hasNext()) {
				TileEntityFurnace furnaceTile = iterator.next();
				if (furnaceTile != null && hasLava) {
					furnaceTile.furnaceBurnTime = furnaceTile.currentItemBurnTime = 100;
					if (furnaceTile.furnaceCookTime == 0) {
						furnaceTile.furnaceCookTime -= 101;
					} else if (furnaceTile.furnaceCookTime == -1) {
						furnaceTile.furnaceCookTime = 1;
					}
					
					if (worldObj.getBlockId(furnaceTile.xCoord, furnaceTile.yCoord, furnaceTile.zCoord) != Block.furnaceBurning.blockID) {
						BlockFurnace.updateFurnaceBlockState(true, worldObj, furnaceTile.xCoord, furnaceTile.yCoord, furnaceTile.zCoord);
						updateFurnaces();
					}
				}
			iterator.remove(); // avoids a ConcurrentModificationException
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
		this.furnaceTiles.clear();
		
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int id = this.worldObj.getBlockId(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (id == Block.furnaceIdle.blockID || id == Block.furnaceBurning.blockID) {
				this.furnaceTiles.add((TileEntityFurnace) worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ));
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