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
	public static int blockSkylightID;

	public static Block blockMachine;
	public static Block blockIO;
	public static Block blockProxy;
	public static Block blockWireless;
	public static Block blockSkylight;
	
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
		Property skylight = config.getBlock("blockID_skylight", 505);
		blockSkylightID = skylight.getInt(505);
	}
	
	public static void initializeBlocks() {
		if (blockMachineID != 0) {
			blockMachine = new BlockMachine(blockMachineID).setUnlocalizedName("machine");
			GameRegistry.registerBlock(blockMachine, ItemBlockMachine.class, "blockMachine");
		}
		
		if (blockIOID != 0) {
			blockIO = new BlockIO(blockIOID).setUnlocalizedName("io");
			GameRegistry.registerBlock(blockIO, "blockIO");
		}
		
		if (blockProxyID != 0) {
			blockProxy = new BlockSideProxy(blockProxyID).setUnlocalizedName("sideProxy");
			GameRegistry.registerBlock(blockProxy, "blockProxy");
		}
		
		if (blockWirelessID != 0) {
			blockWireless = new BlockRemoteInventory(blockWirelessID).setUnlocalizedName("remoteInventory");
			GameRegistry.registerBlock(blockWireless, "blockRemote");
		}
		
		if (blockSkylightID != 0) {
			blockSkylight = new BlockSkylight(blockSkylightID).setUnlocalizedName("skylight");
			GameRegistry.registerBlock(blockSkylight, "blockSkylight");
		}
	}
	
}