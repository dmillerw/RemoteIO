package com.dmillerw.remoteIO.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.block.tile.TileEntitySideProxy;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSideProxy extends BlockContainer {

	public static float MIN = 0.1875F;
	public static float MAX = 0.8125F;
	
	public Icon[] icons;
	
	public BlockSideProxy(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileEntitySideProxy tile = (TileEntitySideProxy) world.getBlockTileEntity(x, y, z);

		if (tile != null && tile.fullyValid()) {
			return this.icons[1];
		} else {
			return this.icons[0];
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[meta];
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
		this.icons = new Icon[3];
		
		this.icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIOBlank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIO");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIOInactive");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySideProxy();
	}
	
}
