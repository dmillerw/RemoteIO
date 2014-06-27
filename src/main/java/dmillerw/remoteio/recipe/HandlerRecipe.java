package dmillerw.remoteio.recipe;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.ModHelper;
import dmillerw.remoteio.core.helper.RecipeHelper;
import dmillerw.remoteio.item.HandlerItem;
import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author dmillerw
 */
public class HandlerRecipe {

	public static void initialize() {
		// REMOTE INTERFACE
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerBlock.remoteInterface),
				" E ",
				"RGR",
				"RRR",
				'E', Items.ender_pearl,
				'R', Items.redstone,
				'G', Blocks.gold_block
		);

		// REMOTE INVENTORY
		GameRegistry.addShapelessRecipe(
				new ItemStack(HandlerBlock.remoteInventory),
				new ItemStack(HandlerBlock.remoteInterface),
				new ItemStack(HandlerItem.wirelessTransmitter)
		);

		// IO TOOL
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.ioTool),
				" I ",
				"RSI",
				"IR ",
				'I', Items.iron_ingot,
				'R', Items.redstone,
				'S', Items.stick
		);

		// LOCATION CHIP
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.locationChip),
				"R",
				"P",
				"G",
				'R', Items.redstone,
				'P', Items.paper,
				'G', Items.gold_nugget
		);

		// BLANK PLATE
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.blankPlate),
				"III",
				'I', Items.iron_ingot
		);

		// WIRELESS TRANSMITTER
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.wirelessTransmitter),
				" E ",
				"S  ",
				"IRI",
				'E', Items.ender_pearl,
				'S', Items.stick,
				'I', Items.iron_ingot,
				'R', Items.redstone
		);

		// TRANSFER TYPE - ITEM
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.transferChip, 1, TransferType.MATTER_ITEM),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', Blocks.chest,
				'C', HandlerItem.locationChip
		);

		// TRANSFER TYPE - WATER
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.transferChip, 1, TransferType.MATTER_FLUID),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', Items.bucket,
				'C', HandlerItem.locationChip
		);

		// TRANSFER TYPE - ESSENTIA
		RecipeHelper.addDependentOreRecipe(
				"Thaumcraft",
				new ItemStack(HandlerItem.transferChip, 1, TransferType.MATTER_ESSENTIA),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', ModHelper.getThaumcraftItem("itemEssence", OreDictionary.WILDCARD_VALUE),
				'C', HandlerItem.locationChip
		);

		// TRANSFER TYPE - IC2
		for (ItemStack cable : getIC2Cables()) {
			RecipeHelper.addDependentOreRecipe(
					"IC2",
					new ItemStack(HandlerItem.transferChip, 1, TransferType.ENERGY_IC2),
					" B ",
					"ICI",
					'B', HandlerItem.blankPlate,
					'I', cable,
					'C', HandlerItem.locationChip
			);
		}

		// TRANSFER TYPE - BC
		for (ItemStack pipe : getBCPipes()) {
			RecipeHelper.addDependentOreRecipe(
					"BuildCraft|Core",
					new ItemStack(HandlerItem.transferChip, 1, TransferType.ENERGY_BC),
					" B ",
					"ICI",
					'B', HandlerItem.blankPlate,
					'I', pipe,
					'C', HandlerItem.locationChip
			);
		}

		// TRANSFER TYPE - RF
		RecipeHelper.addDependentOreRecipe(
				"CoFHAPI|energy",
				new ItemStack(HandlerItem.transferChip, 1, TransferType.ENERGY_RF),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', Items.redstone,
				'C', HandlerItem.locationChip
		);

		// UPGRADE TYPE - REMOTE CAMOUFLAGE
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.upgradeChip, 1, UpgradeType.REMOTE_CAMO),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', Items.ender_pearl,
				'C', HandlerItem.locationChip
		);

		// UPGRADE TYPE - SIMPLE CAMOUFLAGE
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.upgradeChip, 1, UpgradeType.REMOTE_CAMO),
				" B ",
				"ICI",
				'B', HandlerItem.blankPlate,
				'I', Blocks.stone,
				'C', HandlerItem.locationChip
		);

		// UPGRADE TYPE - REMOTE ACCESS
		RecipeHelper.addOreRecipe(
				new ItemStack(HandlerItem.upgradeChip, 1, UpgradeType.REMOTE_ACCESS),
				"B",
				"C",
				"R",
				'B', HandlerItem.blankPlate,
				'C', HandlerItem.locationChip,
				'R', HandlerItem.wirelessTransmitter
		);
	}

	private static ItemStack[] getIC2Cables() {
		if (Loader.isModLoaded("IC2")) {
			String[] cableTypes = new String[] {"copper", "insulatedCopper", "gold", "insulatedGold", "iron", "insulatedIron", "insulatedTin", "glassFiber", "tin"};
			ItemStack[] cables = new ItemStack[cableTypes.length];
			boolean failed = false;

			try {
				for (int i=0; i<cableTypes.length; i++) {
					cables[i] = IC2Items.getItem(cableTypes[i] + "CableItem");
				}
			} catch(Exception ex) {
				FMLLog.warning("Tried to get IC2 power cables, but failed! IC2 support will not be available!");
				return new ItemStack[0];
			}

			return cables;
		}

		return new ItemStack[0];
	}

	public static ItemStack[] getBCPipes() {
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
				FMLLog.warning("Tried to get Buildcraft power pipes, but failed! Buildcraft support will not be available!");
				return new ItemStack[0];
			}

			return pipes;
		}

		return new ItemStack[0];
	}
}
