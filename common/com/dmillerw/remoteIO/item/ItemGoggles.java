package com.dmillerw.remoteIO.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;

import com.dmillerw.remoteIO.core.CreativeTabRIO;

public class ItemGoggles extends ItemArmor {

	public static EnumArmorMaterial GOGGLES = EnumHelper.addArmorMaterial("remoteIOGoggles", 20, new int[] { 1, 3, 2, 1 }, 15);

	public static boolean isPlayerWearing(EntityPlayer player) {
		ItemStack stack = player.getCurrentItemOrArmor(4);
		return stack != null && stack.getItem() instanceof ItemGoggles;
	}
	
	public ItemGoggles(int id) {
		super(id, GOGGLES, 0, 0);
		setCreativeTab(CreativeTabRIO.tab);
	}
	
	// TODO TEXTURES
	
}
