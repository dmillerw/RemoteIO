package com.dmillerw.remoteIO.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.dmillerw.remoteIO.block.BlockMachine;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemHandler {

	public static int itemToolID;
	public static int itemUpgradeID;
	public static int itemComponentID;
	public static int itemGogglesID;
	public static int itemTransmitterID;
	
	public static Item itemTool;
	public static Item itemUpgrade;
	public static Item itemComponent;
	public static Item itemGoggles;
	public static Item itemTransmitter;
	
	public static void handleConfig(Configuration config) {
		config.addCustomCategoryComment(Configuration.CATEGORY_ITEM, "Set any ID to 0 to disable item");
		
		Property tool = config.getItem("itemID_tool", 5000);
		itemToolID = tool.getInt(5000);
		Property upgrade = config.getItem("itemID_upgrade", 5001);
		itemUpgradeID = upgrade.getInt(5001);
		Property component = config.getItem("itemID_component", 5002);
		itemComponentID = component.getInt(5002);
		Property goggles = config.getItem("itemID_goggles", 5003);
		itemGogglesID = goggles.getInt(5003);
		Property transmitter = config.getItem("itemID_transmitter", 5004);
		itemTransmitterID = transmitter.getInt(5004);
	}
	
	public static void initializeItems() {
		if (itemToolID != 0) {
			itemTool = new ItemTool(itemToolID).setUnlocalizedName("itemTool");
			GameRegistry.registerItem(itemTool, "itemTool");
			LanguageRegistry.addName(itemTool, "IO Linker");
		}
		
		if (itemUpgradeID != 0) {
			itemUpgrade = new ItemUpgrade(itemUpgradeID).setUnlocalizedName("itemUpgrade");
			GameRegistry.registerItem(itemUpgrade, "itemUpgrade");
			for (Upgrade upgrade : Upgrade.values()) {
				LanguageRegistry.addName(upgrade.toItemStack(), upgrade.localizedName);
			}
		}
		
		if (itemComponentID != 0) {
			itemComponent = new ItemComponent(itemComponentID).setUnlocalizedName("itemComponent");
			GameRegistry.registerItem(itemComponent, "itemComponent");
			for (int i=0; i<ItemComponent.names.length; i++) {
				LanguageRegistry.addName(new ItemStack(itemComponent, 1, i), ItemComponent.names[i]);
			}
		}
		
		if (itemGogglesID != 0) {
			itemGoggles = new ItemGoggles(itemGogglesID).setUnlocalizedName("itemGoggles");
			GameRegistry.registerItem(itemGoggles, "itemGoggles");
			LanguageRegistry.addName(itemGoggles, "IO Goggles");
		}
		
		if (itemTransmitterID != 0) {
			itemTransmitter = new ItemTransmitter(itemTransmitterID).setUnlocalizedName("itemTransmitter");
			GameRegistry.registerItem(itemTransmitter, "itemTransmitter");
			LanguageRegistry.addName(itemTransmitter, "Wireless Transceiver");
		}
	}
	
}