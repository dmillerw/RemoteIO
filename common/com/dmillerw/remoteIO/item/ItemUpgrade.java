package com.dmillerw.remoteIO.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ItemStackReference;
import com.dmillerw.remoteIO.lib.ModInfo;

public class ItemUpgrade extends Item {

	public Icon[] icons;
	
	public ItemUpgrade(int id) {
		super(id);
		
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idk) {
		Upgrade upgrade = Upgrade.values()[stack.getItemDamage()];
		String[] desc = I18n.getString("upgrade." + upgrade.texture + ".desc").split("\n");
		for (String str : desc) {
			if (upgrade == Upgrade.BLANK) {
				return;
			}
			if (upgrade == Upgrade.RANGE_T1) {
				str = str.replace("%1", ""+RemoteIO.instance.rangeUpgradeT1Boost);
			}
			if (upgrade == Upgrade.RANGE_T2) {
				str = str.replace("%1", ""+RemoteIO.instance.rangeUpgradeT2Boost);
			}
			if (upgrade == Upgrade.RANGE_T3) {
				str = str.replace("%1", ""+RemoteIO.instance.rangeUpgradeT3Boost);
			}
			if (upgrade == Upgrade.RANGE_WITHER) {
				str = str.replace("%1", ""+RemoteIO.instance.rangeUpgradeWitherBoost);
			}
			list.add(str);
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return Upgrade.values()[stack.getItemDamage()] == Upgrade.RANGE_WITHER;
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		return this.icons[damage];
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade.recipeComponents != null || upgrade == Upgrade.BLANK) {
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
		return "upgrade." + Upgrade.values()[stack.getItemDamage()].texture;
	}	
	
	public static enum Upgrade {
		BLANK(
			"blank", 
			null
		), 
		
		ITEM(
			"item", 
			new ItemStack[] {new ItemStack(Block.chest)}
		), 
				
		FLUID(
			"fluid",
			new ItemStack[] {new ItemStack(Item.bucketEmpty)}
		), 
				
		POWER_MJ(
			"powerBC",
			new ItemStack[0]
		), 
				
		RANGE_T1(
			"rangeT1",
			new ItemStack[] {new ItemStack(Item.glowstone)}
		), 
				
		CROSS_DIMENSIONAL(
			"dimension", 
			new ItemStack[] {new ItemStack(Block.obsidian), new ItemStack(Block.enderChest)}
		), 
				
		ISIDED_AWARE(
			"side", 
			new ItemStack[] {new ItemStack(Block.hopperBlock), Upgrade.ITEM.toItemStack()}
		), 
				
		REDSTONE(
			"redstone",
			new ItemStack[] {new ItemStack(Item.redstone)}
		), 
				
		CAMO(
			"camo",
			new ItemStack[] {ItemStackReference.COMPONENT_CAMO}
		), 
				
		LOCK(
			"lock",
			new ItemStack[] {ItemStackReference.COMPONENT_LOCK, new ItemStack(Block.chest)}
		), 
				
		POWER_RF(
			"powerRF", 
			new ItemStack[0]
		), 
				
		POWER_EU(
			"powerEU",
			new ItemStack[0]
		),
		
		POWER_UE(
			"powerUE",
			new ItemStack[0]
		),
		
		LINK_PERSIST(
			"persist",
			new ItemStack[] {ItemStackReference.COMPONENT_LOCK, new ItemStack(Item.enderPearl)}
		),
		
		RANGE_T2(
			"rangeT2",
			new ItemStack[0]
		),
		
		RANGE_T3(
			"rangeT3",
			new ItemStack[0]
		),
		
		RANGE_WITHER(
			"rangeWither",
			new ItemStack[0]
		);
		
		public String texture;
		
		public ItemStack[] recipeComponents;
		
		private Upgrade(String texture, ItemStack[] recipeComponents) {
			this.texture = texture;
			this.recipeComponents = recipeComponents;
		}
		
		public ItemStack toItemStack() {
			return new ItemStack(ItemHandler.itemUpgrade, 1, this.ordinal());
		}
		
	}
	
}
