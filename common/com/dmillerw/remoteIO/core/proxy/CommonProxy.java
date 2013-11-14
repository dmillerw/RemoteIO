package com.dmillerw.remoteIO.core.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileEntityHeater;
import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.block.tile.TileEntityReservoir;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
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
			GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockHeater), new Object[] {"SIS", "IFI", "SBS", 'S', Block.cobblestone, 'I', Block.fenceIron, 'F', Block.furnaceIdle, 'B', Item.bucketLava});
		}
		
		if (RemoteIO.instance.config.blockReservoirID != 0) {
			GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.blockReservoir), new Object[] {"SFS", "FFF", "SBS", 'S', Block.cobblestone, 'F', Block.glass, 'B', Item.bucketWater});
		}
		
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.itemTool), new Object[] {"EB ", "BI ", "  R", 'E', Item.enderPearl, 'B', Item.dyePowder, 'I', Item.ingotIron, 'R', Item.redstone});
	
		// Blank Upgrade
		GameRegistry.addRecipe(new ShapedOreRecipe(Upgrade.BLANK.toItemStack(), "GCG", "IRI", "IRI", 'G', Item.goldNugget, 'I', Item.ingotIron, 'C', "dyeGreen", 'R', Item.redstone));
	
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade.recipeComponents != null && upgrade.recipeComponents.length == 1) {
				GameRegistry.addRecipe(upgrade.toItemStack(), "C", "U", "C", 'C', upgrade.recipeComponents[0], 'U', Upgrade.BLANK.toItemStack());
			} else if (upgrade.recipeComponents != null && upgrade.recipeComponents.length == 2) {
				GameRegistry.addRecipe(upgrade.toItemStack(), "C", "U", "D", 'C', upgrade.recipeComponents[0], 'D', upgrade.recipeComponents[1], 'U', Upgrade.BLANK.toItemStack());
				GameRegistry.addRecipe(upgrade.toItemStack(), "D", "U", "C", 'C', upgrade.recipeComponents[0], 'D', upgrade.recipeComponents[1], 'U', Upgrade.BLANK.toItemStack());
			}
		}
		
		// Iron Rod component
		GameRegistry.addRecipe(new ItemStack(RemoteIO.instance.config.itemComponent, 1, 2), new Object[] {" I", "I ", 'I', Item.ingotIron});
		OreDictionary.registerOre("rodIron", new ItemStack(RemoteIO.instance.config.itemComponent, 1, 2));
		
		// Camo Component
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RemoteIO.instance.config.itemComponent, 1, 0), new Object[] {"LSL", "SIS", "LSL", 'L', "logWood", 'S', "stone", 'I', Item.ingotIron}));
		
		// Padlock Component
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(RemoteIO.instance.config.itemComponent, 1, 1), new Object[] {"   ", " R ", " I ", 'R', "rodIron", 'I', Item.ingotIron}));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// If EnderStorage detected, replace Dimensional Upgrade recipe
		if (Loader.isModLoaded("EnderStorage")) {
			ItemStack obsidian = new ItemStack(Block.obsidian);
			ItemStack enderChest = null;
			
			try {
				Class clazz = Class.forName("codechicken.enderstorage.EnderStorage");
				enderChest = new ItemStack((Block)clazz.getDeclaredField("blockEnderChest").get(clazz), 1, OreDictionary.WILDCARD_VALUE);
			} catch(Exception ex) {
				// IGNORING
			}
			
			if (enderChest != null) {
				GameRegistry.addRecipe(Upgrade.CROSS_DIMENSIONAL.toItemStack(), "C", "U", "D", 'C', enderChest, 'D', obsidian, 'U', Upgrade.BLANK.toItemStack());
				GameRegistry.addRecipe(Upgrade.CROSS_DIMENSIONAL.toItemStack(), "D", "U", "C", 'C', enderChest, 'D', obsidian, 'U', Upgrade.BLANK.toItemStack());
			} else {
				FMLLog.warning("[Remote IO] Tried to get Ender Storage EnderChest, but failed!");
			}
		}
	}

}
