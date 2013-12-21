package com.dmillerw.remoteIO.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

public class BlockSkylight extends Block {

	private Icon[] icons;
	
	public BlockSkylight(int id) {
		super(id, Material.glass);
		
		setHardness(2F);
		setResistance(2F);
		setCreativeTab(CreativeTabRIO.tab);
	}
	
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote) {
			updateState(world, x, y, z);
		}
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int causeID) {
		if (!world.isRemote) {
			updateState(world, x, y, z);
		}
	}
	
	private void updateState(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		
		if (powered && meta == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			world.updateAllLightTypes(x, y, z);
		} else if (!powered && meta == 1) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			world.updateAllLightTypes(x, y, z);
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public int getRenderBlockPass() {
        return 1;
    }
	
	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0 ? 255 : 0;
	}
	
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[meta];
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "other/glass");
	}
	
}
