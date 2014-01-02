package com.dmillerw.remoteIO.core.helper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.Arrays;

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

}
