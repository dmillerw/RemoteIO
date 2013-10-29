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

import com.dmillerw.remoteIO.block.render.RenderBlockReservoir;
import com.dmillerw.remoteIO.block.tile.TileEntityReservoir;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReservoir extends BlockContainer {

	public Icon iconFrame;
	public Icon iconFrameDark;
	public Icon iconGlass;
	
	public BlockReservoir(int id) {
		super(id, Material.rock);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityReservoir tile = (TileEntityReservoir) world.getBlockTileEntity(x, y, z);
		
		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntityReservoir tile = (TileEntityReservoir) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return Block.waterStill.getIcon(side, meta);
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
		this.iconFrame = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrame");
		this.iconFrameDark = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrameDark");
		this.iconGlass = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirGlass");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return RenderBlockReservoir.renderID;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReservoir();
	}

}
