package com.dmillerw.remoteIO.item;

import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;

public enum Upgrade {

	ITEM("item", "Item", "Allows for the basic transport of items"),
	FLUID("fluid", "Fluid", "Allows for the basic transport of fluids"),
	POWER_BC("powerBC", "Buildcraft Power", "Allows for the transfer of BC power (MJ)"),
	RANGE("range", "Range", "Increases the range at which the IO block can connect", "Each upgrade increases the range by 8 blocks"),
	CROSS_DIMENSIONAL("crossDimensional", "Cross Dimensional", "Allows the IO block to connect across dimensions"),
	ISIDED_AWARE("iSidedAware", "Side Awareness", "Allows the IO block to determine side input/output");
	
	public String texture;
	public String localizedName;
	
	public String[] description;
	
	private Upgrade(String texture, String localizedName, String ... description) {
		this.texture = texture;
		this.localizedName = "Upgrade: " + localizedName;
		this.description = description;
	}
	
	public ItemStack toItemStack() {
		return new ItemStack(RemoteIO.instance.config.itemUpgradeID + 256, 1, this.ordinal());
	}
	
}
