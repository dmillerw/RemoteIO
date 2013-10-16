package com.dmillerw.remoteIO.core.config;

import com.dmillerw.remoteIO.block.BlockHeater;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

public class RIOConfiguration {

	private final Configuration config;
	
	public int blockRIOID;
	public int blockHeaterID;
	
	public int itemUpgradeID;
	
	public Block blockRIO;
	public Block blockHeater;
	
	public Item itemUpgrade;
	
	public RIOConfiguration(Configuration config) {
		this.config = config;
	}
	
	public void scanConfig() {
		this.blockRIOID = config.getBlock("block_id.RIO", 600).getInt(600);
		this.blockHeaterID = config.getBlock("block_id.heater", 601).getInt(601);
		this.itemUpgradeID = config.getItem("item_id.upgrade", 6001).getInt(6001);
		
		// Item/Block init
		this.blockHeater = new BlockHeater(blockHeaterID).setUnlocalizedName("blockHeater");
		GameRegistry.registerBlock(this.blockHeater, "blockHeater");
		LanguageRegistry.addName(this.blockHeater, "Heater");
	}
	
}
