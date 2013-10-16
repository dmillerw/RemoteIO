package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.core.CreativeTabRIO;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRIO extends BlockContainer {

	public BlockRIO(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return null;
	}
	
}
