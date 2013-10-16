package com.dmillerw.remoteIO.item;

import net.minecraft.item.Item;
import net.minecraft.util.Icon;

import com.dmillerw.remoteIO.core.CreativeTabRIO;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTool extends Item {

	public Icon icon;
	
	public ItemTool(int id) {
		super(id);
		
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int meta) {
		return this.icon;
	}
	
}
