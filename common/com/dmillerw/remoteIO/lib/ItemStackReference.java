package com.dmillerw.remoteIO.lib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.item.ItemHandler;

public class ItemStackReference {

	/* DYE */
	public static final ItemStack DYE_ANY = getDye(OreDictionary.WILDCARD_VALUE);

	public static final ItemStack DYE_BLACK = getDye(0);
	public static final ItemStack DYE_RED = getDye(1);
	public static final ItemStack DYE_GREEN = getDye(2);
	public static final ItemStack DYE_BROWN = getDye(3);
	public static final ItemStack DYE_BLUE = getDye(4);
	public static final ItemStack DYE_PURPLE = getDye(5);
	public static final ItemStack DYE_CYAN = getDye(6);
	public static final ItemStack DYE_SILVER = getDye(7);
	public static final ItemStack DYE_GRAY = getDye(8);
	public static final ItemStack DYE_PINK = getDye(9);
	public static final ItemStack DYE_LIME = getDye(10);
	public static final ItemStack DYE_YELLOW = getDye(11);
	public static final ItemStack DYE_LIGHT_BLUE = getDye(12);
	public static final ItemStack DYE_MAGENTA = getDye(13);
	public static final ItemStack DYE_ORANGE = getDye(14);
	public static final ItemStack DYE_WHITE = getDye(15);
	
	public static final ItemStack COMPONENT_CAMO = getStack(ItemHandler.itemComponentID + 256, 0);
	public static final ItemStack COMPONENT_LOCK = getStack(ItemHandler.itemComponentID + 256, 1);
	public static final ItemStack COMPONENT_IRONROD = getStack(ItemHandler.itemComponentID + 256, 2);
	public static final ItemStack COMPONENT_MONITOR = getStack(ItemHandler.itemComponentID + 256, 3);
	
	public static final ItemStack BLOCK_IO = getStack(BlockHandler.blockIOID, 0);
	
	private static ItemStack getDye(int meta) {
		return getStack(Item.dyePowder.itemID, meta);
	}
			
	private static ItemStack getStack(int id, int meta) {
		return new ItemStack(id, 1, meta);
	}
	
}