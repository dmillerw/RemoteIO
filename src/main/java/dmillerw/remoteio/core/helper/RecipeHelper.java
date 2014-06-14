package dmillerw.remoteio.core.helper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author dmillerw
 */
public class RecipeHelper {

	public static void addOreRecipe(ItemStack output, Object ... inputs) {
		for (int i=0; i<inputs.length; i++) {
			if (inputs[i] instanceof Block) {
				ItemStack stack = new ItemStack((Block)inputs[i]);
				String tag = OreDictionary.getOreName(OreDictionary.getOreID(stack));
				inputs[i] = tag;
			} else if (inputs[i] instanceof Item) {
				ItemStack stack = new ItemStack((Item)inputs[i]);
				String tag = OreDictionary.getOreName(OreDictionary.getOreID(stack));
				inputs[i] = tag;
			} else if (inputs[i] instanceof ItemStack) {
				String tag = OreDictionary.getOreName(OreDictionary.getOreID((ItemStack)inputs[i]));
				inputs[i] = tag;
			}
		}

		GameRegistry.addRecipe(output, inputs);
	}

	public static void addDependentOreRecipe(String modId, ItemStack output, Object ... inputs) {
		for (int i=0; i<inputs.length; i++) {
			if (inputs[i] instanceof Block) {
				ItemStack stack = new ItemStack((Block)inputs[i]);
				String tag = OreDictionary.getOreName(OreDictionary.getOreID(stack));
				inputs[i] = tag;
			} else if (inputs[i] instanceof Item) {
				ItemStack stack = new ItemStack((Item)inputs[i]);
				String tag = OreDictionary.getOreName(OreDictionary.getOreID(stack));
				inputs[i] = tag;
			} else if (inputs[i] instanceof ItemStack) {
				String tag = OreDictionary.getOreName(OreDictionary.getOreID((ItemStack)inputs[i]));
				inputs[i] = tag;
			}
		}

		addDependentRecipe(modId, output, inputs);
	}

	public static void addDependentRecipe(String modId, ItemStack output, Object ... inputs) {
		if (Loader.isModLoaded(modId)) {
			GameRegistry.addShapedRecipe(output, inputs);
		}
	}
}
