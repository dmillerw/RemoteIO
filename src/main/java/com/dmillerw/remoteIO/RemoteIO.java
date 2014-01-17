package com.dmillerw.remoteIO;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.core.handler.*;
import com.dmillerw.remoteIO.core.helper.EnergyHelper;
import com.dmillerw.remoteIO.core.helper.IOLogger;
import com.dmillerw.remoteIO.core.helper.LocalizationHelper;
import com.dmillerw.remoteIO.core.proxy.ISidedProxy;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.lib.ModInfo;
import com.dmillerw.remoteIO.turtle.TurtleBridgeUpgrade;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.turtle.api.TurtleAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
@NetworkMod(channels={ModInfo.ID}, serverSideRequired=true, clientSideRequired=false, packetHandler= PacketHandler.class)
public class RemoteIO {

	@Instance(ModInfo.ID)
	public static RemoteIO instance;
	
	@SidedProxy(serverSide=ModInfo.COMMON_PROXY, clientSide=ModInfo.CLIENT_PROXY)
	public static ISidedProxy proxy;

	public File configDir;

	public int defaultRange = 8;
	
	public int rangeUpgradeT1Boost = 8;
	public int rangeUpgradeT2Boost = 16;
	public int rangeUpgradeT3Boost = 64;
	public int rangeUpgradeWitherBoost = 1024;

    public boolean witherNeedsDragonEgg = true;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "RemoteIO");
		
		File newConfig = new File(configDir, "RemoteIO.cfg");
		
		if (event.getSuggestedConfigurationFile().exists() && !newConfig.exists()) {
			IOLogger.info("Detected old config file, Attempting to automatically migrate");
			
			if (!newConfig.exists()) {
				try {
					configDir.mkdirs();
					newConfig.createNewFile();
				} catch(Exception ex) {}
			}
			
			int returnValue;
			
			try {
				if ((returnValue = IOUtils.copy(new FileInputStream(event.getSuggestedConfigurationFile()), new FileOutputStream(newConfig))) > 0) {
					IOLogger.info("Successfully migrated old config");
				} else {
					IOLogger.warn("Failed to migrate old config file! Default IDs and settings will be used!");
					IOLogger.warn("Reason: Copy method returned bad value. Value: " + returnValue);
				}
			} catch(Exception ex) {
				IOLogger.warn("Failed to migrate old config file! Default IDs and settings will be used!");
				IOLogger.warn("Reason: " + ex.getLocalizedMessage());
			}
		}
		
		Configuration config = new Configuration(newConfig);
		
		config.load();
		
		defaultRange = config.get("general", "defaultRange", 8, "Default range remote blocks have, without any Range upgrades").getInt(8);
		rangeUpgradeT1Boost = config.get("general", "rangeUpgradeT1Boost", 8, "How much a T1 range upgrade boosts the range by").getInt(8);
		rangeUpgradeT2Boost = config.get("general", "rangeUpgradeT2Boost", 16, "How much a T2 range upgrade boosts the range by").getInt(16);
		rangeUpgradeT3Boost = config.get("general", "rangeUpgradeT3Boost", 64, "How much a T3 range upgrade boosts the range by").getInt(64);
		rangeUpgradeWitherBoost = config.get("general", "rangeUpgradeWitherBoost", 1024, "How much a wither range upgrade boosts the range by").getInt(1024);

        witherNeedsDragonEgg = config.get("general", "witherUpgradeRequiresEgg", true, "Whether the Uber Range Upgrade needs a dragon egg. If disabled, simply uses two nether stars").getBoolean(true);

        TileIOCore.fuelPerStack = config.get("power.io", "fuelPerStack", 3600, "How much fuel the defined fuel item will add").getInt(72000);
        TileIOCore.fuelPerTick = config.get("power.io", "fuelPerTick", 1, "How much fuel should be consumed every second").getInt(1);
        int stackID = config.get("power.io", "fuel_itemID", Item.enderPearl.itemID, "Item ID for the fuel item").getInt(Item.enderPearl.itemID);
        int stackMeta = config.get("power.io", "fuel_damageValue", 0, "Optional damage value for fuel item").getInt(0);
        TileIOCore.fuelStack = new ItemStack(stackID, 1, stackMeta);

        TileIOCore.rfPerFuel = config.get("power.io", "rfPerFuel", 350, "How much RF is equivalent to one fuel unit").getInt(350);
        TileIOCore.euPerFuel = config.get("power.io", "euPerFuel", 5,   "How much EU is equivalent to one fuel unit").getInt(5);

        TileIOCore.consumeOnlyWhenActive = config.get("power.io", "consumeOnlyWhenActive", true, "Whether blocks should only consume fuel when actively connected to something").getBoolean(true);

        EnergyHelper.rfPerTurtleMove = config.get("power.turtle", "rfPerTurtleMove", 350, "How much RF is equivalent to one turtle movement operation").getInt(350);
        EnergyHelper.euPerTurtleMove = config.get("power.turtle", "euPerTurtleMove", 5,   "How much EU is equivalent to one turtle movement operation").getInt(5);

		BlockHandler.handleConfig(config);
		BlockHandler.initializeBlocks();
		
		ItemHandler.handleConfig(config);
		ItemHandler.initializeItems();

        for (ItemUpgrade.Upgrade upgrade : ItemUpgrade.Upgrade.values()) {
            upgrade.enabled = config.get("upgrade", "upgrade." + upgrade.texture.toLowerCase(), true).getBoolean(true);
        }

		if (config.hasChanged()) {
			config.save();
		}
		
		NetworkRegistry.instance().registerGuiHandler(RemoteIO.instance, new GuiHandler());
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
        TickRegistry.registerTickHandler(BlockTracker.getInstance(), Side.SERVER);

        GameRegistry.registerCraftingHandler(new CraftingHandler());

		MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
		
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		LocalizationHelper.initializeLocalization();
		LocalizationHelper.initializeUserLocalization(new File(configDir, "lang"));
		
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		
		if (Loader.isModLoaded("ComputerCraft")) {
		    TurtleAPI.registerUpgrade(new TurtleBridgeUpgrade());
		}
	}
	
}
