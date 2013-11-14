package com.dmillerw.remoteIO.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.ModInfo;

public class ItemComponent extends Item {

	public static String[] names = new String[] {"Counter-illumination Panel", "Padlock", "Iron Rod"};
	public static String[] subNames = new String[] {"camo", "padlock", "ironRod"};
	
	public Icon[] icons;
	
	public ItemComponent(int id) {
		super(id);
		
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public Icon getIconFromDamage(int damage) {
		return this.icons[damage];
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i=0; i<subNames.length; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[Upgrade.values().length];
		
		for (int i=0; i<subNames.length; i++) {
			this.icons[i] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "component/" + this.subNames[i]);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.component." + subNames[stack.getItemDamage()] + ".name";
	}
	
}
