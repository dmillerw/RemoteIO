package com.dmillerw.remoteIO.core.proxy;

import appeng.api.Blocks;
import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.api.documentation.DocumentationRegistry;
import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.*;
import com.dmillerw.remoteIO.core.helper.IOLogger;
import com.dmillerw.remoteIO.core.helper.RecipeHelper;
import com.dmillerw.remoteIO.core.helper.StackHelper;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.ItemStackReference;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.Items;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

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
		
		if (BlockHandler.blockBridgeID != 0) {
		    GameRegistry.registerTileEntity(TileTurtleBridge.class, "blockTurtleBridge");
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
	
        /* NORMAL UPGRADE RECIPES */

        Map<Upgrade, ItemStack[]> upgradeRecipes = new HashMap<Upgrade, ItemStack[]>();
        upgradeRecipes.put(Upgrade.ITEM,         new ItemStack[] {new ItemStack(Block.chest)});
        upgradeRecipes.put(Upgrade.FLUID,        new ItemStack[] {new ItemStack(Item.bucketEmpty)});
        upgradeRecipes.put(Upgrade.ISIDED_AWARE, new ItemStack[] {new ItemStack(Block.hopperBlock)});
        upgradeRecipes.put(Upgrade.REDSTONE,     new ItemStack[] {new ItemStack(Item.redstone)});
        upgradeRecipes.put(Upgrade.CAMO,         new ItemStack[] {ItemStackReference.COMPONENT_CAMO});
        upgradeRecipes.put(Upgrade.LOCK,         new ItemStack[] {ItemStackReference.COMPONENT_LOCK});

        for (Upgrade upgrade : Upgrade.values()) {
            if (upgrade.enabled) {
                ItemStack[] components = upgradeRecipes.get(upgrade);
                if (components != null && components.length == 1) {
                    RecipeHelper.addOreRecipe(upgrade.toItemStack(), "C", "U", "C", 'C', components[0], 'U', Upgrade.BLANK.toItemStack());
                } else if (components != null && components.length == 2) {
                    RecipeHelper.addOreRecipe(upgrade.toItemStack(), "C", "U", "D", 'C', components[0], 'D', components[1], 'U', Upgrade.BLANK.toItemStack());
                    RecipeHelper.addOreRecipe(upgrade.toItemStack(), "D", "U", "C", 'C', components[0], 'D', components[1], 'U', Upgrade.BLANK.toItemStack());
                }
            }
        }

        /* END */

		/* RANGE UPGRADE RECIPES */
		// T2
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T2.toItemStack(), "GRG", "RUR", "GRG", 'G', Item.glowstone, 'R', Item.redstone, 'U', Upgrade.RANGE_T1.toItemStack());
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T2.toItemStack(), "RGR", "GUG", "RGR", 'G', Item.glowstone, 'R', Item.redstone, 'U', Upgrade.RANGE_T1.toItemStack());
		
		// T3
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T3.toItemStack(), "GRG", "RUR", "GRG", 'G', Item.glowstone, 'R', Item.netherQuartz, 'U', Upgrade.RANGE_T2.toItemStack());
		RecipeHelper.addOreRecipe(Upgrade.RANGE_T3.toItemStack(), "RGR", "GUG", "RGR", 'G', Item.glowstone, 'R', Item.netherQuartz, 'U', Upgrade.RANGE_T2.toItemStack());
		
		// WITHER
		RecipeHelper.addOreRecipe(Upgrade.RANGE_WITHER.toItemStack(), "E", "U", "S", 'E', RemoteIO.instance.witherNeedsDragonEgg ? Block.dragonEgg : Item.netherStar, 'U', Upgrade.RANGE_T3.toItemStack(), 'S', Item.netherStar);
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
		if (Loader.isModLoaded("EnderStorage") && Upgrade.CROSS_DIMENSIONAL.enabled) {
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
		if (Loader.isModLoaded("BuildCraft|Core") && Upgrade.POWER_MJ.enabled) {
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
		if (Loader.isModLoaded("IC2") && Upgrade.POWER_EU.enabled) {
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
		if (Loader.isModLoaded("ThermalExpansion") && Upgrade.POWER_RF.enabled) {
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

        if (Loader.isModLoaded("AppliedEnergistics") && Upgrade.AE.enabled) {
            for (ItemStack cable : Blocks.blkCable_Colored) {
                RecipeHelper.addOreRecipe(Upgrade.AE.toItemStack(), "C", "U", "C", 'C', cable, 'U', Upgrade.BLANK.toItemStack());
            }
        }

        if (Loader.isModLoaded("ComputerCraft")) {
            Block blockPeripheral = null;
            try {
                Class ccBlocksClass = Class.forName("dan200.ComputerCraft").getDeclaredClasses()[1];
                blockPeripheral = (Block) ccBlocksClass.getDeclaredField("peripheral").get(null);
            } catch (Exception ex) {
                IOLogger.warn("Tried to get ComputerCraft peripheral block, but failed. The Turtle Bridge will not be available!");
            }

            if (blockPeripheral != null) {
                RecipeHelper.addOreRecipe(new ItemStack(BlockHandler.blockBridge), "P", "I", 'P', blockPeripheral, 'I', BlockHandler.blockWireless);
            }
        }

        /* DOCUMENTATION HANDLING */
        String[] linkerDocumentation = new String[] {
                "The IO Linker is primarily used in conjunction with the IO Block, and is used to link various other blocks in the world to it.",
                "Functionality is simple. Right click on any block in the world (sneaking if necessary to avoid GUIs) to link that block to this tool. Then simply come back to your IO blocks and right-click on it with the tool in hand.",
                "Ta-da! Instantly linked!",
                "For more info, see the page on the IO Block"
        };

        String[] transceiverDocumentation = new String[] {
                "This item has one simple use, and that is to allow for the Remote Inventory block to connect to the inventory of the Player holding this.",
                "To link it to yourself, simply shift right-click with it in hand, and then ensure it always remains in your inventory when the Remote Inventory block is accessed.",
                "This item also has the added functionality of being able to access any block's GUI remotely. Simply right-click on any IO Block that's linked to a block, and the linked block will act as though you had simply right-clicked it."
        };

        String[] goggleDocumentation = new String[] {
                "Provided it's in the same dimension, the Player wearing these goggles will be able to see a path connecting any IO Block to the block it's linked to."
        };

        String[] ioDocumentation = new String[] {
                "The core feature of this mod.",
                "This block can be used to remotely access multiple aspects of a block placed elsewhere in the world, as though the block was right there.",
                "In an attempt to keep a sense of balance, this block can't do anything by itself. By installing various upgrades you can add various functionalities to this block. By default, it's unable to do anything, and has a limited connection range of " + RemoteIO.instance.defaultRange + " blocks.",
                "This block, as well as all other IO blocks, requires power. Power information can be found under Misc -> Fuel"
        };

        String[] removeInvDocumentation = new String[] {
                "This block has similar features to that of the IO block, but has a far more specific purpose.",
                "Instead of connecting with other blocks in the world, this connects with other Players, specifically their inventories. Simply right-click this block with a linked Wireless Transceiver to link the Player to it.",
                "Similar to the IO Block, this block can take upgrades, and has a limited range of " + RemoteIO.instance.defaultRange + " blocks."
        };

        String[] turtleBridgeDocumentation = new String[] {
                "This block has similar features to that of the IO block, but only connects to ComputerCraft turtles.",
                "By combining a turtle with any IO upgrade, you can get a Bridged Turtle. Wrapping the attached peripheral and calling sync() while above this bridge block will link the turtle to this bridge.",
                "Once linked, you have access to the turtles inventory, as well as its interal power source, provided the right upgrades are used."
        };

        String[] heaterDocumentation = new String[] {
                "This block allows one to power a vanilla furnace simply with two lava source blocks, but at the cost of efficiency.",
                "Simply place this block down, along with two adjacent lava source blocks. It will then precede to power any adjacent furnaces."
        };

        String[] reservoirDocumentation = new String[] {
                "This block allows one to have a compact infinite source of water.",
                "Simply place this block down, along with two adjacent water source blocks. It will then precede to fill it's internal tank with water, which can then be pumped out."
        };

        String[] skylightDocumentation = new String[] {
                "Skylights can be used to block out light, but allow it to filter through at the change of a redstone signal.",
                "Simply place any number of them down, and power one with a redstone signal. All those connected will then change their state to match the one being powered."
        };

        String[] sidedProxyDocumentation = new String[] {
                "This is a dumbed down version of the IO Block.",
                "Simply place it down, and link it to any adjacent block using the IO Linker. It will then relay Items and Fluids through the side you linked it to, similar to Sneaky Pipes."
        };

        String[] powerDocumentation = new String[] {
                "POWER REQUIREMENTS ARE CONFIGURABLE IN THE CONFIG!",
                "",
                "All IO blocks require power either in the form of the fuel item (" + TileIOCore.fuelStack.getDisplayName() + ") which provides " + TileIOCore.fuelPerStack + " fuel, or by placing an IC2 battery/crystal or RF flux capaciter in the charge slot",
                "IC2 power provides 1 generic fuel unit per " + TileIOCore.euPerFuel + " EU, and RF power provides 1 generic fuel unit per " + TileIOCore.rfPerFuel + " RF",
                "While active, IO blocks use " + TileIOCore.fuelPerTick + " power per tick",
        };

        String[] itemUpgrade = new String[] {
                "IO Block/Side Proxy:",
                "Allows for the basic transport of items",
                "",
                "Remote Inventory:",
                "Allows for the transport of items to/from the connected player's inventory",
                "",
                "Turtle Bridge:",
                "Allows for the transport of items to/from the synced turtle",
        };

        String[] sidedUpgrade = new String[] {
                "IO Block/Side Proxy:",
                "Item transfer will obey any rules placed on the side they're inserted into",
        };

        String[] fluidUpgrade = new String[] {
                "IO Block/Side Proxy:",
                "Allows for the basic transport of fluids",
        };

        String[] dimensionalUpgrade = new String[] {
                "Any:",
                "Will allow for IO block to access inventories outside of its own dimension.",
                "If inventory is outside of the IO block's dimension, range is NOT taken into account",
        };

        DocumentationRegistry.document("Misc", "Power", powerDocumentation);

        DocumentationRegistry.document("Upgrades", "Item", itemUpgrade);
        DocumentationRegistry.document("Upgrades", "Side Awareness", sidedUpgrade);
        DocumentationRegistry.document("Upgrades", "Fluid", fluidUpgrade);
        DocumentationRegistry.document("Upgrades", "Cross Dimension", dimensionalUpgrade);

        DocumentationRegistry.document(new ItemStack(ItemHandler.itemTool), linkerDocumentation);
        DocumentationRegistry.document(new ItemStack(ItemHandler.itemTransmitter), transceiverDocumentation);
        DocumentationRegistry.document(new ItemStack(ItemHandler.itemGoggles), goggleDocumentation);

        DocumentationRegistry.document(new ItemStack(BlockHandler.blockIO), ioDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockWireless), removeInvDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockMachine, 0, 0), heaterDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockMachine, 0, 1), reservoirDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockSkylight), skylightDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockProxy), sidedProxyDocumentation);
        DocumentationRegistry.document(new ItemStack(BlockHandler.blockBridge), turtleBridgeDocumentation);
    }

    public void ioPathFX(World world, TileEntity tile, double tx, double ty, double tz, float speed) {}

}
