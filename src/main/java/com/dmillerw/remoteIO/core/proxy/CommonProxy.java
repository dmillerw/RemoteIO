package com.dmillerw.remoteIO.core.proxy;

import appeng.api.Blocks;
import com.dmillerw.remoteIO.api.documentation.DocumentationRegistry;
import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.*;
import com.dmillerw.remoteIO.core.helper.IOLogger;
import com.dmillerw.remoteIO.core.helper.RecipeHelper;
import com.dmillerw.remoteIO.core.helper.StackHelper;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.Items;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CommonProxy implements ISidedProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if (BlockHandler.blockIOID != 0) {
			GameRegistry.registerTileEntity(TileIO.class, "blockIO");
		}
		
		if (BlockHandler.blockMachineID != 0) {
			GameRegistry.registerTileEntity(TileHeater.class, "blockMachine_heater");
			GameRegistry.registerTileEntity(TileReservoir.class, "blockMachine_reservoir");
		}
		
		if (BlockHandler.blockProxyID != 0) {
			GameRegistry.registerTileEntity(TileSideProxy.class, "blockProxy");
		}
		
		if (BlockHandler.blockWirelessID != 0) {
			GameRegistry.registerTileEntity(TileRemoteInventory.class, "blockRemote");
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		if (BlockHandler.blockIOID != 0) {
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockIO, 2, 0), "SIS", "ESE", "SIS", 'S', Block.stone, 'I', Block.blockIron, 'E', Item.enderPearl);
		}
		
		if (BlockHandler.blockMachineID != 0) {
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockMachine, 1, 0), "SIS", "IFI", "SBS", 'S', Block.cobblestone, 'I', Block.fenceIron, 'F', Block.furnaceIdle, 'B', Item.bucketLava);
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockMachine, 1, 1), "SFS", "FFF", "SBS", 'S', Block.cobblestone, 'F', Block.glass, 'B', Item.bucketWater);
		}
		
		if (BlockHandler.blockProxyID != 0) {
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockProxy, 4, 0), " E ", "1I2", 'E', Item.enderPearl, '1', Upgrade.ISIDED_AWARE.toItemStack(), '2', Upgrade.FLUID.toItemStack(), 'I', Block.hopperBlock);
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockProxy, 4, 0), " E ", "1I2", 'E', Item.enderPearl, '2', Upgrade.ISIDED_AWARE.toItemStack(), '1', Upgrade.FLUID.toItemStack(), 'I', Block.hopperBlock);
		}
		
		if (BlockHandler.blockSkylightID != 0) {
			RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockSkylight, 8), "SSS", "GGG", "SSS", 'S', Block.stone, 'G', Block.glass);
		}
		
		// Wrench
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemTool), "EB ", "BI ", "  R", 'E', Item.enderPearl, 'B', Item.dyePowder, 'I', Item.ingotIron, 'R', Item.redstone);
		GameRegistry.addShapelessRecipe(new ItemStack(ItemHandler.itemTool), ItemHandler.itemTool);
		
		// IO Goggles
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemGoggles), "L L", "I I", "GEG", 'L', Item.leather, 'I', Item.ingotIron, 'G', Block.thinGlass, 'E', Item.enderPearl);
		
		// Blank Upgrade
		RecipeHelper.addOreRecipe(StackHelper.resize(Upgrade.BLANK.toItemStack(), 16), "GCG", "IRI", "IRI", 'G', Item.goldNugget, 'I', Item.ingotIron, 'C', "dyeGreen", 'R', Item.redstone);
	
		for (Upgrade upgrade : Upgrade.values()) {
			if (upgrade.recipeComponents != null && upgrade.recipeComponents.length == 1) {
				RecipeHelper.addOreRecipe(upgrade.toItemStack(), "C", "U", "C", 'C', upgrade.recipeComponents[0], 'U', Upgrade.BLANK.toItemStack());
			} else if (upgrade.recipeComponents != null && upgrade.recipeComponents.length == 2) {
				RecipeHelper.addOreRecipe(upgrade.toItemStack(), "C", "U", "D", 'C', upgrade.recipeComponents[0], 'D', upgrade.recipeComponents[1], 'U', Upgrade.BLANK.toItemStack());
				RecipeHelper.addOreRecipe(upgrade.toItemStack(), "D", "U", "C", 'C', upgrade.recipeComponents[0], 'D', upgrade.recipeComponents[1], 'U', Upgrade.BLANK.toItemStack());
			}
		}
		
		/* RANGE UPGRADE RECIPES */
		// T2
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T2.toItemStack(), "GRG", "RUR", "GRG", 'G', Item.glowstone, 'R', Item.redstone, 'U', Upgrade.RANGE_T1.toItemStack());
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T2.toItemStack(), "RGR", "GUG", "RGR", 'G', Item.glowstone, 'R', Item.redstone, 'U', Upgrade.RANGE_T1.toItemStack());
		
		// T3
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T3.toItemStack(), "GRG", "RUR", "GRG", 'G', Item.glowstone, 'R', Item.netherQuartz, 'U', Upgrade.RANGE_T2.toItemStack());
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T3.toItemStack(), "RGR", "GUG", "RGR", 'G', Item.glowstone, 'R', Item.netherQuartz, 'U', Upgrade.RANGE_T2.toItemStack());
		
		// WITHER
		RecipeHelper.addOreRecipe(Upgrade.RANGE_WITHER.toItemStack(), "E", "U", "S", 'E', Block.dragonEgg, 'U', Upgrade.RANGE_T3.toItemStack(), 'S', Item.netherStar);
		/* END */
		
		// Wireless Transceiver
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemTransmitter), " E ", "III", "IRI", 'E', Item.enderPearl, 'I', Item.ingotIron, 'R', Item.redstone);
		
		// Remote Inventory
		RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockWireless), " U ", "III", "ITI", 'U', Upgrade.ITEM.toItemStack(), 'I', Item.ingotIron, 'T', new ItemStack(ItemHandler.itemTransmitter));
		
		// Iron Rod component
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemComponent, 6, 2), "I  ", " I ", "  I", 'I', Item.ingotIron);
		OreDictionary.registerOre("rodIron", new ItemStack(ItemHandler.itemComponent, 1, 2));
		
		// Camo Component
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemComponent, 1, 0), "LSL", "SIS", "LSL", 'L', "logWood", 'S', "stone", 'I', Item.ingotIron);
		
		// Padlock Component
		RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemComponent, 1, 1), "   ", " R ", " I ", 'R', "rodIron", 'I', Item.ingotIron);

        // Documentation Item
        RecipeHelper.addOreRecipe(new ItemStack(ItemHandler.itemScreen), "SSS", "DDD", "GGG", 'S', Block.stone, 'D', new ItemStack(Item.dyePowder, 1, OreDictionary.WILDCARD_VALUE), 'G', Block.glass);
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
				RecipeHelper.addOreRecipe(Upgrade.CROSS_DIMENSIONAL.toItemStack(), "C", "U", "D", 'C', enderChest, 'D', obsidian, 'U', Upgrade.BLANK.toItemStack());
				RecipeHelper.addOreRecipe(Upgrade.CROSS_DIMENSIONAL.toItemStack(), "D", "U", "C", 'C', enderChest, 'D', obsidian, 'U', Upgrade.BLANK.toItemStack());
			} else {
				IOLogger.warn("Tried to get Ender Storage EnderChest, but failed!");
			}
		}
		
		// If Buildcraft detected, add BC Power Upgrade recipe
		if (Loader.isModLoaded("BuildCraft|Core")) {
			String[] pipeTypes = new String[] {"Wood", "Cobblestone", "Stone", "Quartz", "Iron", "Gold", "Diamond"};
			ItemStack[] pipes = new ItemStack[pipeTypes.length];
			boolean failed = false;
			
			try {
				Class clazz = Class.forName("buildcraft.BuildCraftTransport");
				
				for (int i=0; i<pipeTypes.length; i++) {
					pipes[i] = new ItemStack((Item)clazz.getDeclaredField("pipePower" + pipeTypes[i]).get(clazz));
				}
			} catch(Exception ex) {
				IOLogger.warn("Tried to get Buildcraft power pipes, but failed! Buildcraft support will not be available!");
				IOLogger.warn("Reason: " + ex.getMessage());
				failed = true;
			}
			
			if (!failed) {
				for (ItemStack pipe : pipes) {
					RecipeHelper.addOreRecipe(Upgrade.POWER_MJ.toItemStack(), "C", "U", "C", 'C', pipe, 'U', Upgrade.BLANK.toItemStack());
				}
			}
		}
		
		// If IC2 detected, add EU Power Upgrade recipe
		if (Loader.isModLoaded("IC2")) {
			String[] cableTypes = new String[] {"copper", "insulatedCopper", "gold", "insulatedGold", "iron", "insulatedIron", "insulatedTin", "glassFiber", "tin"};
			ItemStack[] cables = new ItemStack[cableTypes.length];
			boolean failed = false;
			
			try {
				for (int i=0; i<cableTypes.length; i++) {
					cables[i] = Items.getItem(cableTypes[i] + "CableItem");
				}
			} catch(Exception ex) {
				IOLogger.warn("Tried to get IC2 power cables, but failed! IC2 support will not be available!");
				IOLogger.warn("Reason: " + ex.getMessage());
				failed = true;
			}
			
			if (!failed) {
				for (ItemStack cable : cables) {
					RecipeHelper.addOreRecipe(Upgrade.POWER_EU.toItemStack(), "C", "U", "C", 'C', cable, 'U', Upgrade.BLANK.toItemStack());
				}
			}
		}
		
		// If ThermalExpansion detected, add RF Power Upgrade recipe
		if (Loader.isModLoaded("ThermalExpansion")) {
			final String conduitPrefix = "conduitEnergy";
			String[] conduitStrings = new String[] {"Basic", "Hardened", "Reinforced"};
			ItemStack[] conduits = new ItemStack[conduitStrings.length];
			boolean failed = false;
			
			try {
				Class clazz = Class.forName("thermalexpansion.part.conduit.ItemConduitPart");
				for (int i=0; i<conduitStrings.length; i++) {
					conduits[i] = (ItemStack) clazz.getDeclaredField(conduitPrefix + conduitStrings[i]).get(clazz);
				}
			} catch(Exception ex) {
				IOLogger.warn("Tried to get Thermal Expansion power conduits, but failed! Thermal Expansion support will not be available!");
				IOLogger.warn("Reason: " + ex.getMessage());
				failed = true;
			}
			
			if (!failed) {
				for (ItemStack conduit : conduits) {
					RecipeHelper.addOreRecipe(Upgrade.POWER_RF.toItemStack(), "C", "U", "C", 'C', conduit, 'U', Upgrade.BLANK.toItemStack());
				}
			}
		}

        if (Loader.isModLoaded("AppliedEnergistics")) {
            for (ItemStack cable : Blocks.blkCable_Colored) {
                RecipeHelper.addOreRecipe(Upgrade.AE.toItemStack(), "C", "U", "C", 'C', cable, 'U', Upgrade.BLANK.toItemStack());
            }
        }

        /* DOCUMENTATION HANDLING */
        String[] linkerDocumentation = new String[] {
                "The IO Linker is primarily used in conjunction with the IO Block, and is used to link various other blocks in the world to it.",
                "Functionality is simple. Right click on any block in the world (sneaking if necessary to avoid GUIs) to link that block to this tool. Then simply come back to your IO blocks and right-click on it with the tool in hand.",
                "Ta-da! Instantly linked!",
                "For more info, see the page on the IO Block"
        };
        DocumentationRegistry.addDocumentation("LINKER_TOOL", linkerDocumentation);
	}

}
