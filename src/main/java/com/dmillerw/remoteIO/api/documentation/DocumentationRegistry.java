package com.dmillerw.remoteIO.api.documentation;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class DocumentationRegistry {

    public static final String CATEGORY_BLOCK = "Blocks";
    public static final String CATEGORY_ITEM = "Items";

    private static Map<String, Category> categories = new HashMap<String, Category>();

    public static void documentWithRecipe(String category, String name, ItemStack stack, String ... documentation) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<documentation.length; i++) {
            sb.append(documentation[i]);
            if (i != documentation.length - 1) {
                sb.append("\n");
            }
        }
        documentWithRecipe(category, name, stack, sb.toString());
    }

    public static void documentWithRecipe(String category, String name, ItemStack stack, String documentation) {
        if (!categories.containsKey(category)) {
            categories.put(category, new Category(category));
        }
        categories.get(category).push(new Documentation(name, documentation, stack));
    }

    public static void document(ItemStack stack, String ... documentation) {
        if (stack.itemID <= 4095) {
            document(CATEGORY_BLOCK, stack, documentation);
        } else {
            document(CATEGORY_ITEM, stack, documentation);
        }
    }

    public static void document(String category, ItemStack stack, String ... documentation) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<documentation.length; i++) {
            sb.append(documentation[i]);
            if (i != documentation.length - 1) {
                sb.append("\n");
            }
        }
        document(category, stack, sb.toString());
    }

    public static void document(String category, ItemStack stack, String documentation) {
        if (!categories.containsKey(category)) {
            categories.put(category, new Category(category));
        }
        categories.get(category).push(new Documentation(stack.getDisplayName(), documentation, stack));
    }

    public static void document(String category, String name, String ... documentation) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<documentation.length; i++) {
            sb.append(documentation[i]);
            if (i != documentation.length - 1) {
                sb.append("\n");
            }
        }
        document(category, name, sb.toString());
    }

    public static void document(String category, String name, String documentation) {
        if (!categories.containsKey(category)) {
            categories.put(category, new Category(category));
        }
        categories.get(category).push(new Documentation(name, documentation));
    }

    public static Category getCategory(String key) {
        return categories.get(key);
    }

    public static Category[] getCategories() {
        List<Category> categories = new ArrayList<Category>();
        for (Map.Entry<String, Category> cat : DocumentationRegistry.categories.entrySet()) {
            categories.add(cat.getValue());
        }
        return categories.toArray(new Category[categories.size()]);
    }

}
