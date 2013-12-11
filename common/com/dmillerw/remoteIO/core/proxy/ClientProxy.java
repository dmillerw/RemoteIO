package com.dmillerw.remoteIO.core.proxy;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.render.RenderBlockMachine;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		if (BlockHandler.blockMachineID != 0) {
			RenderingRegistry.registerBlockHandler(new RenderBlockMachine());
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
}
