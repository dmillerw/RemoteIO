package com.dmillerw.remoteIO.block.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityHeater extends TileEntityCore {

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
			
			for (TileEntityFurnace furnaceTile : furnaceTiles) {
				if (furnaceTile != null && hasLava) {
					furnaceTile.furnaceBurnTime = furnaceTile.currentItemBurnTime = 100;
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
		updateLava();
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setBoolean("hasLava", this.hasLava);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		this.hasLava = nbt.getBoolean("hasLava");
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
