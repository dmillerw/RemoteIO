package com.dmillerw.remoteIO.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.dmillerw.remoteIO.block.render.RenderBlockHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHeater extends BlockContainer {

	public Icon iconBars;
	
	public BlockHeater(int id) {
		super(id, Material.rock);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityHeater tile = (TileEntityHeater) world.getBlockTileEntity(x, y, z);
		
		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntityHeater tile = (TileEntityHeater) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return Block.lavaStill.getIcon(side, meta);
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.iconBars = register.registerIcon(ModInfo.RESOURCE_PREFIX + "heaterBars");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return RenderBlockHeater.renderID;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityHeater();
	}

}
