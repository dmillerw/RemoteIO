package com.dmillerw.remoteIO.core.proxy;

import com.dmillerw.remoteIO.block.tile.TileEntityHeater;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements ISidedProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityHeater.class, "blockHeater");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
