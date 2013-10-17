package com.dmillerw.remoteIO.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityIO;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements ISidedProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityHeater.class, "blockHeater");
		GameRegistry.registerTileEntity(TileEntityIO.class, "blockIO");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockHeater), new Object[] {"SSS", "SFS", "SBS", 'S', Block.cobblestone, 'F', Block.furnaceIdle, 'B', Item.bucketLava});
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockRIO, 2, 0), new Object[] {"SIS", "ESE", "SIS", 'S', Block.stone, 'I', Block.blockIron, 'E', Item.enderPearl});
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.itemTool), new Object[] {"EB ", "BI ", "  R", 'E', Item.enderPearl, 'B', Item.dyePowder, 'I', Item.ingotIron, 'R', Item.redstone});
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
