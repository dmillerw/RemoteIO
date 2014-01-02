package com.dmillerw.remoteIO.core.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ISidedProxy {

	public void preInit(FMLPreInitializationEvent event);
	
	public void init(FMLInitializationEvent event);
	
	public void postInit(FMLPostInitializationEvent event);

    public void ioPathFX(World world, TileEntity tile, double tx, double ty, double tz, float speed);

}
