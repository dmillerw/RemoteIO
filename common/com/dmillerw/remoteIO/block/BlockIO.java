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

import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockIO extends BlockContainer {

	public Icon[] icons;
	
	public BlockIO(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		if (tile != null && tile.validCoordinates) {
			return this.icons[0];
		} else {
			return this.icons[1];
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[1];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIO");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIOInactive");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityIO();
	}
	
}
