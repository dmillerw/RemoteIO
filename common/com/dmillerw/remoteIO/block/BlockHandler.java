package com.dmillerw.remoteIO.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockHandler {

	public static int blockMachineID;
	public static int blockIOID;
	public static int blockProxyID;
	public static int blockWirelessID;

	public static Block blockMachine;
	public static Block blockIO;
	public static Block blockProxy;
	public static Block blockWireless;
	
	public static void handleConfig(Configuration config) {
		config.addCustomCategoryComment(Configuration.CATEGORY_BLOCK, "Set any ID to 0 to disable block");
		
		Property machine = config.getBlock("blockID.blockMachine", 500);
		blockMachineID = machine.getInt(500);
		Property io = config.getBlock("blockID.blockIO", 501);
		blockIOID = io.getInt(501);
		Property proxy = config.getBlock("blockID_proxy", 502);
		blockProxyID = proxy.getInt(503);
		Property wireless = config.getBlock("blockID_wireless", 504);
		blockWirelessID = wireless.getInt(504);
	}
	
	public static void initializeBlocks() {
		if (blockMachineID != 0) {
			blockMachine = new BlockMachine(blockMachineID).setUnlocalizedName("blockMachine");
			GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "blockMachine");
			for (int i=0; i<BlockMachine.INTERNAL_NAMES.length; i++) {
				LanguageRegistry.addName(new ItemStack(blockMachine, 1, i), BlockMachine.NAMES[i]);
			}
		}
		
		if (blockIOID != 0) {
			blockIO = new BlockIO(blockIOID).setUnlocalizedName("blockIO");
			GameRegistry.registerBlock(blockIO, "blockIO");
			LanguageRegistry.addName(blockIO, "IO Block");
		}
		
		if (blockProxyID != 0) {
			blockProxy = new BlockSideProxy(blockProxyID).setUnlocalizedName("blockProxy");
			GameRegistry.registerBlock(blockProxy, "blockProxy");
			LanguageRegistry.addName(blockProxy, "Side Proxy");
		}
		
		if (blockWirelessID != 0) {
			blockWireless = new BlockRemoteInventory(blockWirelessID).setUnlocalizedName("blockRemote");
			GameRegistry.registerBlock(blockWireless, "blockRemote");
			LanguageRegistry.addName(blockWireless, "Remote Inventory");
		}
	}
	
}