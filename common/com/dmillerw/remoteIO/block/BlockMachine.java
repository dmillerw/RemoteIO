package com.dmillerw.remoteIO.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.dmillerw.remoteIO.block.render.RenderBlockMachine;
import com.dmillerw.remoteIO.block.tile.TileCore;
import com.dmillerw.remoteIO.block.tile.TileHeater;
import com.dmillerw.remoteIO.block.tile.TileReservoir;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMachine extends BlockCore {

	public static final String[] INTERNAL_NAMES = new String[] {"heater", "reservoir"};
	public static final String[] NAMES = new String[] {"Lava-Powered Heater", "Water Reservoir"};
	
	public Icon iconBars;
	public Icon iconBarsDark;
	
	public Icon iconFrame;
	public Icon iconFrameDark;
	public Icon iconGlass;
	
	public BlockMachine(int id) {
		super(id);
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
	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		for (int i=0; i<INTERNAL_NAMES.length; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		switch(meta) {
		case 0: return Block.lavaStill.getIcon(side, 0);
		case 1: return Block.waterStill.getIcon(side, 0);
		default: return null;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.iconBars = register.registerIcon(ModInfo.RESOURCE_PREFIX + "heaterBars");
		this.iconBarsDark = register.registerIcon(ModInfo.RESOURCE_PREFIX + "heaterBarsDark");
		this.iconFrame = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrame");
		this.iconFrameDark = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrameDark");
		this.iconGlass = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirGlass");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return RenderBlockMachine.renderID;
	}
	
	@Override
	public TileCore getTile(int meta) {
		switch(meta) {
		case 0: return new TileHeater();
		case 1: return new TileReservoir();
		default: return null;
		}
	}

}