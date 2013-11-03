package com.dmillerw.remoteIO.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;

import cpw.mods.fml.common.registry.GameRegistry;

public enum Upgrade {

	BLANK("blank", "Blank Upgrade", null),
	ITEM("item", "Item", new ItemStack(Block.chest), "Allows for the basic transport of items"),
	FLUID("fluid", "Fluid", new ItemStack(Item.bucketEmpty), "Allows for the basic transport of fluids"),
	POWER_BC("powerBC", "Buildcraft Power", new ItemStack(Item.redstone), "Allows for the transfer of BC power (MJ)"),
	RANGE("range", "Range", new ItemStack(Item.glowstone), "Increases the range at which the IO block can connect", "Each upgrade increases the range by 8 blocks"),
	CROSS_DIMENSIONAL("crossDimensional", "Cross Dimensional", new ItemStack(Block.obsidian), "Allows the IO block to connect across dimensions"),
	ISIDED_AWARE("iSidedAware", "Side Awareness", new ItemStack(Block.hopperBlock), "Allows the IO block to determine side input/output"),
	REDSTONE("redstone", "Redstone", new ItemStack(Item.redstoneRepeater), "Allows for the toggle of the remote connection via redstone");
	
	public String texture;
	public String localizedName;
	
	public ItemStack recipeComponent;
	
	public String[] description;
	
	private Upgrade(String texture, String localizedName, ItemStack recipeComponent, String ... description) {
		this.texture = texture;
		this.localizedName = "Upgrade: " + localizedName;
		this.recipeComponent = recipeComponent;
		this.description = description;
	}
	
	public ItemStack toItemStack() {
		return new ItemStack(RemoteIO.instance.config.itemUpgradeID + 256, 1, this.ordinal());
	}
	
}
