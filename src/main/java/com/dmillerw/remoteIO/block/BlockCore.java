package com.dmillerw.remoteIO.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.block.tile.TileCore;
import com.dmillerw.remoteIO.core.CreativeTabRIO;

public abstract class BlockCore extends BlockContainer {

	public BlockCore(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			if (tile != null && tile instanceof TileCore) {
				return ((TileCore)tile).onBlockActivated(player);
			}
		}
		
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		
		if (tile != null && tile instanceof TileCore) {
			((TileCore)tile).onNeighborBlockUpdate();
		}
	}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null && tile instanceof TileCore) {
            ((TileCore)tile).onBlockPlacedBy(entity, stack);
        }
    }

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile != null && tile instanceof TileCore) {
			((TileCore)tile).onNeighborBlockUpdate();
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile != null && tile instanceof TileCore) {
			((TileCore)tile).onBlockBreak();
		}
		
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	
	public abstract TileCore getTile(int meta);
	
	/* IGNORE */
	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return getTile(meta);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
}