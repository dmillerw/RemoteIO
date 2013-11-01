package com.dmillerw.remoteIO.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

public class ItemUpgrade extends Item {

	public Icon[] icons;
	
	public ItemUpgrade(int id) {
		super(id);
		
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idk) {
		for (String string : Upgrade.values()[stack.getItemDamage()].description) {
			list.add(string);
		}
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		return this.icons[damage];
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade.recipeComponent != null || upgrade == Upgrade.BLANK) {
				list.add(upgrade.toItemStack());
			}
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[Upgrade.values().length];
		
		for (Upgrade upgrade : Upgrade.values()) {
			this.icons[upgrade.ordinal()] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "upgrade/" + upgrade.texture);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + Upgrade.values()[stack.getItemDamage()] + ".name";
	}	
	
}
