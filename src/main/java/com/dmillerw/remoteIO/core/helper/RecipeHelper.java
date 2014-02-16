package com.dmillerw.remoteIO.core.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class RecipeHelper {

    public static void addOreRecipe(ItemStack output, Object ... input) {
        Object[] newArray = Arrays.copyOf(input, input.length);

        for (int i=0; i<newArray.length; i++) {
            Object object = newArray[i];

            if (object instanceof Block) {
                ItemStack stack = new ItemStack((Block)object);
                String oreName = OreDictionary.getOreName(OreDictionary.getOreID(stack));
                if (oreName != null && !(oreName.isEmpty()) && (!oreName.equalsIgnoreCase("unknown"))) {
                    newArray[i] = oreName;
                }
            } else if (object instanceof Item) {
                ItemStack stack = new ItemStack((Item)object);
                String oreName = OreDictionary.getOreName(OreDictionary.getOreID(stack));
                if (oreName != null && !(oreName.isEmpty()) && (!oreName.equalsIgnoreCase("unknown"))) {
                    newArray[i] = oreName;
                }
            } else if (object instanceof ItemStack) {
                ItemStack stack = ((ItemStack)object).copy();
                String oreName = OreDictionary.getOreName(OreDictionary.getOreID(stack));
                if (oreName != null && !(oreName.isEmpty()) && (!oreName.equalsIgnoreCase("unknown"))) {
                    newArray[i] = oreName;
                }
            }
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(output, newArray));
    }

    @SuppressWarnings("unchecked")
    public static ItemStack[] getFirstRecipeForItem(ItemStack resultingItem) {
        ItemStack[] recipeItems = new ItemStack[9];
        for (IRecipe recipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
            if (recipe == null) continue;

            ItemStack result = recipe.getRecipeOutput();
            if (result == null || !result.isItemEqual(resultingItem)) continue;

            Object[] input = getRecipeInput(recipe);
            if (input == null) continue;

            for (int i = 0; i < input.length; i++)
                recipeItems[i] = convertToStack(input[i]);
            break;

        }
        return recipeItems;
    }

    protected static ItemStack convertToStack(Object obj) {
        ItemStack entry = null;
        if (obj instanceof ItemStack) {
            entry = (ItemStack) obj;
        } else if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<ItemStack> list = (List<ItemStack>) obj;
            if (list.size() > 0) entry = list.get(0);
        }

        if (entry == null) return null;
        entry = entry.copy();
        if (entry.getItemDamage() == OreDictionary.WILDCARD_VALUE) entry.setItemDamage(0);
        return entry;
    }

    @SuppressWarnings("unchecked")
    private static Object[] getRecipeInput(IRecipe recipe) {
        if (recipe instanceof ShapelessOreRecipe) return ((ShapelessOreRecipe) recipe).getInput().toArray();
        else if (recipe instanceof ShapedOreRecipe) return getShapedOreRecipe((ShapedOreRecipe) recipe);
        else if (recipe instanceof ShapedRecipes) return ((ShapedRecipes) recipe).recipeItems;
        else if (recipe instanceof ShapelessRecipes)
            return ((ShapelessRecipes) recipe).recipeItems.toArray(new ItemStack[0]);
        return null;
    }

    private static Object[] getShapedOreRecipe(ShapedOreRecipe recipe) {
        try {
            Field field = ShapedOreRecipe.class.getDeclaredField("width");
            if (field != null) {
                field.setAccessible(true);
                int width = field.getInt(recipe);
                Object[] input = recipe.getInput();
                Object[] grid = new Object[9];
                for (int i = 0, offset = 0, y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++, i++) {
                        if (x < width && offset < input.length) {
                            grid[i] = input[offset];
                            offset++;
                        } else {
                            grid[i] = null;
                        }
                    }
                }
                return grid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
