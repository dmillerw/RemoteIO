package com.dmillerw.remoteIO.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ItemHandler {

	public static int itemToolID;
	public static int itemUpgradeID;
	public static int itemComponentID;
	public static int itemGogglesID;
	public static int itemTransmitterID;
    public static int itemScreenID;
	
	public static Item itemTool;
	public static Item itemUpgrade;
	public static Item itemComponent;
	public static Item itemGoggles;
	public static Item itemTransmitter;
    public static Item itemScreen;
	
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
        Property screen = config.getItem("itemID_screen", 5005);
        itemScreenID = screen.getInt(5005);
	}
	
	public static void initializeItems() {
		if (itemToolID != 0) {
			itemTool = new ItemTool(itemToolID).setUnlocalizedName("ioLinker");
			GameRegistry.registerItem(itemTool, "itemTool");
		}
		
		if (itemUpgradeID != 0) {
			itemUpgrade = new ItemUpgrade(itemUpgradeID).setUnlocalizedName("upgrade");
			GameRegistry.registerItem(itemUpgrade, "itemUpgrade");
		}
		
		if (itemComponentID != 0) {
			itemComponent = new ItemComponent(itemComponentID).setUnlocalizedName("component");
			GameRegistry.registerItem(itemComponent, "itemComponent");
		}
		
		if (itemGogglesID != 0) {
			itemGoggles = new ItemGoggles(itemGogglesID).setUnlocalizedName("ioGoggles");
			GameRegistry.registerItem(itemGoggles, "itemGoggles");
		}
		
		if (itemTransmitterID != 0) {
			itemTransmitter = new ItemTransmitter(itemTransmitterID).setUnlocalizedName("transceiver");
			GameRegistry.registerItem(itemTransmitter, "itemTransmitter");
		}

        if (itemScreenID != 0) {
            itemScreen = new ItemScreen(itemScreenID).setUnlocalizedName("screen");
            GameRegistry.registerItem(itemScreen, "itemScreen");
        }
	}
	
}