package com.dmillerw.remoteIO.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.block.tile.TileEntityReservoir;
import com.dmillerw.remoteIO.item.Upgrade;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements ISidedProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if (RemoteIO.instance.config.blockRIOID != 0) {
			GameRegistry.registerTileEntity(TileEntityIO.class, "blockIO");
		}
		
		if (RemoteIO.instance.config.blockHeaterID != 0) {
			GameRegistry.registerTileEntity(TileEntityHeater.class, "blockHeater");
		}
		
		if (RemoteIO.instance.config.blockReservoirID != 0) {
			GameRegistry.registerTileEntity(TileEntityReservoir.class, "blockReservoir");
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (RemoteIO.instance.config.blockRIOID != 0) {
			GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockRIO, 2, 0), new Object[] {"SIS", "ESE", "SIS", 'S', Block.stone, 'I', Block.blockIron, 'E', Item.enderPearl});
		}
		
		if (RemoteIO.instance.config.blockHeaterID != 0) {
			GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockHeater), new Object[] {"SSS", "SFS", "SBS", 'S', Block.cobblestone, 'F', Block.furnaceIdle, 'B', Item.bucketLava});
		}
		
		if (RemoteIO.instance.config.blockReservoirID != 0) {
			GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockReservoir), new Object[] {"SSS", "SFS", "SBS", 'S', Block.cobblestone, 'F', Block.glass, 'B', Item.bucketWater});
		}
		
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.itemTool), new Object[] {"EB ", "BI ", "  R", 'E', Item.enderPearl, 'B', Item.dyePowder, 'I', Item.ingotIron, 'R', Item.redstone});
	
		// Blank Upgrade
		GameRegistry.addRecipe(Upgrade.BLANK.toItemStack(), "ICI", "IRI", "IRI", 'I', Item.ingotIron, 'C', new ItemStack(Item.dyePowder, 1, 2), 'R', Item.redstone);
	
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade.recipeComponent != null) {
				GameRegistry.addRecipe(upgrade.toItemStack(), "C", "U", "C", 'C', upgrade.recipeComponent, 'U', Upgrade.BLANK.toItemStack());
			}
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
