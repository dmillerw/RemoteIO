package com.dmillerw.remoteIO.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.dmillerw.remoteIO.RemoteIO;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public enum Upgrade {

	BLANK("blank", "Blank Upgrade", null),
	ITEM("item", "Item", new ItemStack[] {new ItemStack(Block.chest)}, "Allows for the basic transport of items"),
	FLUID("fluid", "Fluid", new ItemStack[] {new ItemStack(Item.bucketEmpty)}, "Allows for the basic transport of fluids"),
	POWER_BC("powerBC", "Buildcraft Power", new ItemStack[] {new ItemStack(Item.redstone)}, "Allows for the transfer of BC power (MJ)"),
	RANGE("range", "Range", new ItemStack[] {new ItemStack(Item.glowstone)}, "Increases the range at which the IO block can connect", "Each upgrade increases the range by 8 blocks"),
	CROSS_DIMENSIONAL("crossDimensional", "Cross Dimensional", getEnderchestRecipe(), "Allows the IO block to connect across dimensions"),
	ISIDED_AWARE("iSidedAware", "Side Awareness", new ItemStack[] {new ItemStack(Block.hopperBlock)}, "Allows the IO block to determine side input/output"),
	REDSTONE("redstone", "Redstone", new ItemStack[] {new ItemStack(Item.redstoneRepeater)}, "Allows for the toggle of the remote connection via redstone"),
	CAMO("camo", "Camo", new ItemStack[] {new ItemStack(Block.glass)}, "Allows the IO block to take on the texture of any other block");
	
	public String texture;
	public String localizedName;
	
	public ItemStack[] recipeComponents;
	
	public String[] description;
	
	private Upgrade(String texture, String localizedName, ItemStack[] recipeComponents, String ... description) {
		this.texture = texture;
		this.localizedName = "Upgrade: " + localizedName;
		this.recipeComponents = recipeComponents;
		this.description = description;
	}
	
	public ItemStack toItemStack() {
		return new ItemStack(RemoteIO.instance.config.itemUpgradeID + 256, 1, this.ordinal());
	}
	
	public static ItemStack[] getEnderchestRecipe() {
		ItemStack enderChest = new ItemStack(Block.enderChest);
		
		// If EnderStorage exists, use CB's Ender Chest
		// Otherwise, use vanilla
		if (Loader.isModLoaded("EnderStorage")) {
			Block cbEnderChest = GameRegistry.findBlock("EnderStorage", "enderChest");
			
			if (cbEnderChest != null) {
				enderChest = new ItemStack(cbEnderChest, 1, OreDictionary.WILDCARD_VALUE);
			}
		}
		
		return new ItemStack[] {new ItemStack(Block.obsidian), enderChest};
	}
	
}
