package com.dmillerw.remoteIO;

import net.minecraftforge.common.Configuration;

import com.dmillerw.remoteIO.core.config.RIOConfiguration;
import com.dmillerw.remoteIO.core.proxy.ISidedProxy;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
public class RemoteInteraction {

	@Instance(ModInfo.ID)
	public static RemoteInteraction instance;
	
	@SidedProxy(serverSide=ModInfo.COMMON_PROXY, clientSide=ModInfo.CLIENT_PROXY)
	public static ISidedProxy proxy;
	
	public RIOConfiguration config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.config = new RIOConfiguration(new Configuration(event.getSuggestedConfigurationFile()));
		this.config.scanConfig();
		
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
	
}
