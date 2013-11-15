package com.dmillerw.remoteIO.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.EnumHelper;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGoggles extends ItemArmor {

	public static EnumArmorMaterial GOGGLES = EnumHelper.addArmorMaterial("remoteIOGoggles", 20, new int[] { 1, 3, 2, 1 }, 15);

	public static boolean isPlayerWearing(EntityPlayer player) {
		ItemStack stack = player.getCurrentItemOrArmor(4);
		return stack != null && stack.getItem() instanceof ItemGoggles;
	}

	public Icon icon;
	
	public ItemGoggles(int id) {
		super(id, GOGGLES, 0, 0);
		setCreativeTab(CreativeTabRIO.tab);
	}
	
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return ModInfo.RESOURCE_PREFIX + "textures/armor/gogglesArmor.png";
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int meta) {
		return this.icon;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "itemGoggles");
	}
	
}
