package com.dmillerw.remoteIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraftforge.common.Configuration;

import org.apache.commons.io.IOUtils;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.core.handler.GuiHandler;
import com.dmillerw.remoteIO.core.helper.IOLogger;
import com.dmillerw.remoteIO.core.helper.LocalizationHelper;
import com.dmillerw.remoteIO.core.proxy.ISidedProxy;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION, dependencies="after:EnderStorage")
@NetworkMod(channels={ModInfo.ID}, serverSideRequired=true, clientSideRequired=false)
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
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "RemoteIO");
		
		File newConfig = new File(configDir, "RemoteIO.cfg");
		
		if (event.getSuggestedConfigurationFile().exists() && !newConfig.exists()) {
			IOLogger.info("Detected old config file, Attempting to autmatically migrate");
			
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
		
		BlockHandler.handleConfig(config);
		BlockHandler.initializeBlocks();
		
		ItemHandler.handleConfig(config);
		ItemHandler.initializeItems();
		
		if (config.hasChanged()) {
			config.save();
		}
		
		NetworkRegistry.instance().registerGuiHandler(RemoteIO.instance, new GuiHandler());
		TickRegistry.registerTickHandler(BlockTracker.getInstance(), Side.SERVER);
		
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
	}
	
}
