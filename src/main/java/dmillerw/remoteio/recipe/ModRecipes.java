package dmillerw.remoteio.recipe;

import appeng.api.AEApi;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.ModHelper;
import dmillerw.remoteio.core.helper.RecipeHelper;
import dmillerw.remoteio.lib.DependencyInfo;
import dmillerw.remoteio.lib.ModBlocks;
import dmillerw.remoteio.lib.ModItems;
import ic2.api.item.IC2Items;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author dmillerw
 */
public class ModRecipes {

    public static void initialize() {
        // RESERVOIR
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.machine, 1, 0),
                "SGS",
                "GWG",
                "SGS",
                'S', Blocks.stone,
                'G', Blocks.glass,
                'W', Items.water_bucket
        );

        // LAVA HEATER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.machine, 1, 1),
                "SIS",
                "ILI",
                "SIS",
                'S', Blocks.stone,
                'I', Blocks.iron_bars,
                'L', Items.lava_bucket
        );

        // REMOTE INTERFACE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModBlocks.remoteInterface),
                " E ",
                "RGR",
                "RRR",
                'E', Items.ender_pearl,
                'R', Items.redstone,
                'G', Blocks.gold_block
        );

        // SKY LIGHT
        GameRegistry.addShapedRecipe(
                new ItemStack(ModBlocks.skylight),
                "SGS",
                "GRG",
                "STS",
                'S', Blocks.stone,
                'G', Blocks.glass,
                'R', Items.redstone
        );

        // INTELLIGENT WORKBENCH
        GameRegistry.addShapelessRecipe(
                new ItemStack(ModBlocks.intelligentWorkbench),
                Blocks.crafting_table,
                ModItems.locationChip
        );

        // IO TOOL
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.ioTool),
                " I ",
                "RSI",
                "IR ",
                'I', Items.iron_ingot,
                'R', Items.redstone,
                'S', Items.stick
        );

        // LOCATION CHIP
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.locationChip),
                "R",
                "P",
                "G",
                'R', Items.redstone,
                'P', Items.paper,
                'G', Items.gold_nugget
        );

        // BLANK PLATE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.blankPlate),
                "III",
                'I', Items.iron_ingot
        );

        // WIRELESS TRANSMITTER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.wirelessTransmitter),
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
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_ITEM),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Blocks.chest,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - WATER
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_FLUID),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.bucket,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - ESSENTIA
        RecipeHelper.addDependentOreRecipe(
                "Thaumcraft",
                new ItemStack(ModItems.transferChip, 1, TransferType.MATTER_ESSENTIA),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', ModHelper.getThaumcraftItem("itemEssence", OreDictionary.WILDCARD_VALUE),
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - IC2
        for (ItemStack cable : getIC2Cables()) {
            RecipeHelper.addDependentOreRecipe(
                    "IC2",
                    new ItemStack(ModItems.transferChip, 1, TransferType.ENERGY_IC2),
                    " B ",
                    "ICI",
                    'B', ModItems.blankPlate,
                    'I', cable,
                    'C', ModItems.locationChip
            );
        }

        // TRANSFER TYPE - RF
        RecipeHelper.addDependentOreRecipe(
                "CoFHAPI|energy",
                new ItemStack(ModItems.transferChip, 1, TransferType.ENERGY_RF),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.redstone,
                'C', ModItems.locationChip
        );

        // TRANSFER TYPE - AE2 NETWORK
        if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            Object component = AEApi.instance().blocks().blockController.block();
            if (component == null) {
                component = AEApi.instance().blocks().blockChest.block();
            }

            if (component != null) {
                RecipeHelper.addDependentRecipe(
                        DependencyInfo.ModIds.AE2,
                        new ItemStack(ModItems.transferChip, 1, TransferType.NETWORK_AE),
                        " B ",
                        " C ",
                        " I ",
                        'B', ModItems.blankPlate,
                        'I', component,
                        'C', ModItems.locationChip
                );
            }
        }

        // TRANSFER TYPE - REDSTONE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.transferChip, 1, TransferType.REDSTONE),
                " B ",
                " C ",
                " I ",
                'B', ModItems.blankPlate,
                'I', Blocks.redstone_block,
                'C', ModItems.locationChip
        );

        // UPGRADE TYPE - REMOTE CAMOUFLAGE
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.upgradeChip, 1, UpgradeType.REMOTE_CAMO),
                " B ",
                "ICI",
                'B', ModItems.blankPlate,
                'I', Items.ender_pearl,
                'C', ModItems.locationChip
        );

        // UPGRADE TYPE - REMOTE ACCESS
        RecipeHelper.addOreRecipe(
                new ItemStack(ModItems.upgradeChip, 1, UpgradeType.REMOTE_ACCESS),
                "B",
                "C",
                "R",
                'B', ModItems.blankPlate,
                'C', ModItems.locationChip,
                'R', ModItems.wirelessTransmitter
        );
    }

    private static ItemStack[] getIC2Cables() {
        if (Loader.isModLoaded("IC2")) {
            String[] cableTypes = new String[]{"copper", "insulatedCopper", "gold", "insulatedGold", "iron", "insulatedIron", "insulatedTin", "glassFiber", "tin"};
            ItemStack[] cables = new ItemStack[cableTypes.length];

            try {
                for (int i = 0; i < cableTypes.length; i++) {
                    cables[i] = IC2Items.getItem(cableTypes[i] + "CableItem");
                }
            } catch (Exception ex) {
                FMLLog.warning("Tried to get IC2 power cables, but failed! IC2 support will not be available!");
                return new ItemStack[0];
            }

            return cables;
        }

        return new ItemStack[0];
    }
}
