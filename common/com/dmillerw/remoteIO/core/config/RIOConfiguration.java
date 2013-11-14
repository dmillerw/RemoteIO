package com.dmillerw.remoteIO.core.config;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.dmillerw.remoteIO.block.BlockHeater;
import com.dmillerw.remoteIO.block.BlockIO;
import com.dmillerw.remoteIO.block.BlockReservoir;
import com.dmillerw.remoteIO.item.ItemComponent;
import com.dmillerw.remoteIO.item.ItemGoggles;
import com.dmillerw.remoteIO.item.ItemTool;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class RIOConfiguration {

	private final Configuration config;
	
	public int blockRIOID;
	public int blockHeaterID;
	public int blockReservoirID;
	
	public int itemToolID;
	public int itemUpgradeID;
	public int itemComponentID;
	public int itemGogglesID;
	
	public Block blockRIO;
	public Block blockHeater;
	public Block blockReservoir;
	
	public Item itemTool;
	public Item itemUpgrade;
	public Item itemComponent;
	public Item itemGoggles;
	
	public RIOConfiguration(Configuration config) {
		this.config = config;
	}
	
	public void scanConfig() {
		this.config.load();
		
		this.config.getCategory(Configuration.CATEGORY_BLOCK).setComment("Set any ID to 0 to disable the block entirely");
		
		this.blockRIOID = config.getBlock("block_id.RIO", 600).getInt(600);
		this.blockHeaterID = config.getBlock("block_id.heater", 601).getInt(601);
		this.blockReservoirID = config.getBlock("block_id.reservoir", 602).getInt(602);
		this.itemToolID = config.getItem("item_id.tool", 6000).getInt(6000);
		this.itemUpgradeID = config.getItem("item_id.upgrade", 6001).getInt(6001);
		this.itemComponentID = config.getItem("item_id.component", 6002).getInt(6002);
		this.itemGogglesID = config.getItem("item_id.goggles", 6003).getInt(6003);
		if (this.config.hasChanged()) {
			this.config.save();
		}
		
		// Item/Block init
		if (this.blockRIOID != 0) {
			this.blockRIO = new BlockIO(blockRIOID).setUnlocalizedName("blockIO");
			GameRegistry.registerBlock(this.blockRIO, "blockIO");
			LanguageRegistry.addName(this.blockRIO, "IO Block");
		}
		
		if (this.blockHeaterID != 0) {
			this.blockHeater = new BlockHeater(blockHeaterID).setUnlocalizedName("blockHeater");
			GameRegistry.registerBlock(this.blockHeater, "blockHeater");
			LanguageRegistry.addName(this.blockHeater, "Lava-Powered Heater");
		}
		
		if (this.blockReservoirID != 0) {
			this.blockReservoir = new BlockReservoir(blockReservoirID).setUnlocalizedName("blockReservoir");
			GameRegistry.registerBlock(this.blockReservoir, "blockReservoir");
			LanguageRegistry.addName(this.blockReservoir, "Water Reservoir");
		}
		
		this.itemTool = new ItemTool(itemToolID).setUnlocalizedName("itemTool");
		GameRegistry.registerItem(this.itemTool, "itemTool");
		LanguageRegistry.addName(this.itemTool, "RemoteIO Linker");
		
		this.itemUpgrade = new ItemUpgrade(itemUpgradeID).setUnlocalizedName("itemUpgrade");
		GameRegistry.registerItem(this.itemUpgrade, "itemUpgrade");
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade != Upgrade.BLANK) LanguageRegistry.addName(upgrade.toItemStack(), upgrade.localizedName);
		}
		LanguageRegistry.addName(Upgrade.BLANK.toItemStack(), "Blank Upgrade");
		
		this.itemComponent = new ItemComponent(itemComponentID).setUnlocalizedName("itemComponent");
		GameRegistry.registerItem(this.itemComponent, "itemComponent");
		for (int i=0; i<ItemComponent.names.length; i++) {
			LanguageRegistry.addName(new ItemStack(this.itemComponent, 1, i), ItemComponent.names[i]);
		}
		
		this.itemGoggles = new ItemGoggles(itemGogglesID).setUnlocalizedName("itemGoggles");
		GameRegistry.registerItem(this.itemGoggles, "itemGoggles");
		LanguageRegistry.addName(this.itemGoggles, "IO Goggles");
	}
	
}
