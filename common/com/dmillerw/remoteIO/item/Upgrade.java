package com.dmillerw.remoteIO.item;

import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;

public enum Upgrade {

	ITEM("item", "Item"),
	FLUID("fluid", "Fluid"),
	RANGE("range", "Range"),
	CROSS_DIMENSIONAL("crossDimensional", "Cross Dimensional"),
	ISIDED_AWARE("iSidedAware", "Side Awareness");
	
	public String texture;
	public String localizedName;
	
	private Upgrade(String texture, String localizedName) {
		this.texture = texture;
		this.localizedName = "Upgrade: " + localizedName;
	}
	
	public ItemStack toItemStack() {
		return new ItemStack(RemoteIO.instance.config.itemUpgradeID + 256, 1, this.ordinal());
	}
	
}
