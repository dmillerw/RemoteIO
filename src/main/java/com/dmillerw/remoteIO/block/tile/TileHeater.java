package com.dmillerw.remoteIO.block.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileHeater extends TileCore {

	public boolean hasLava = false;
	public boolean firstLoad = true;
	
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			if (firstLoad) {
				firstLoad = false;
			}
			
			if (this.worldObj.getTotalWorldTime() % 20 == 0) {
				updateLavaSources();
			}
		
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				TileEntity tile = worldObj.getBlockTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
			
				if (tile != null && tile instanceof TileEntityFurnace) {
					TileEntityFurnace furnaceTile = (TileEntityFurnace) tile;
					if (furnaceTile != null && hasLava) {
						furnaceTile.furnaceBurnTime = furnaceTile.currentItemBurnTime = 100;
						if (furnaceTile.furnaceCookTime == 0) {
							furnaceTile.furnaceCookTime -= 101;
						} else if (furnaceTile.furnaceCookTime == -1) {
							furnaceTile.furnaceCookTime = 1;
						}
						
						if (worldObj.getBlockId(furnaceTile.xCoord, furnaceTile.yCoord, furnaceTile.zCoord) != Block.furnaceBurning.blockID) {
							BlockFurnace.updateFurnaceBlockState(true, worldObj, furnaceTile.xCoord, furnaceTile.yCoord, furnaceTile.zCoord);
						}
					}
				}
			}
		}
	}
	
	public void onNeighborBlockUpdate() {
		updateLavaSources();
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
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		nbt.setBoolean("hasLava", this.hasLava);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		this.hasLava = nbt.getBoolean("hasLava");
	}
	
}