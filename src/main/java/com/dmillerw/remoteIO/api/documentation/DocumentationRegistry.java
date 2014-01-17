package com.dmillerw.remoteIO.api.documentation;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class DocumentationRegistry {

    public static class Documentation {
        public final String category;
        public final String documentation;

        public Documentation(String cat, String doc) {
            this.category = cat;
            this.documentation = doc;
        }
    }

    private static Map<ItemStack, String> stackToKeyMapping = new HashMap<ItemStack, String>();
    private static Map<String, Documentation> documentation = new HashMap<String, Documentation>();

    private static boolean containsStack(ItemStack stack) {
        for (Map.Entry<ItemStack, String> entry : stackToKeyMapping.entrySet()) {
            if (entry.getKey().isItemEqual(stack)) {
                return true;
            }
        }

        return false;
    }

    private static String getKeyForStack(ItemStack stack) {
        for (Map.Entry<ItemStack, String> entry : stackToKeyMapping.entrySet()) {
            if (entry.getKey().isItemEqual(stack)) {
                return entry.getValue();
            }
        }

        return "";
    }

    //TODO Add basic formatting abilities

    public static void registerKey(Item item, String key) {
        registerKey(new ItemStack(item), key);
    }

    public static void registerKey(Block block, String key) {
        registerKey(new ItemStack(block), key);
    }

    public static void registerKey(ItemStack stack, String key) {
        stackToKeyMapping.put(stack, key);
    }

    public static void addDocumentation(String key, String cat, String[] docu) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<docu.length; i++) {
            sb.append(docu[i]);
            if (i != docu.length - 1) {
                sb.append("\n");
            }
        }
        addDocumentation(key, cat, sb.toString());
    }

    public static void addDocumentation(String key, String cat, String docu) {
        docu = docu.replace("\n", "\n\n");

        if (!documentation.containsKey(key)) {
            documentation.put(key, new Documentation(cat, docu));
        } else {
            throw new RuntimeException("[RemoteIO] Something tried to register documentation with the key " + key + " but that key is already registered!");
        }
    }

    public static Documentation getDocumentation(ItemStack stack) {
        if (hasDocumentation(stack)) {
            return getDocumentation(getKeyForStack(stack));
        }

        return null;
    }

    private static Documentation getDocumentation(String key) {
        return documentation.get(key);
    }

    public static boolean hasDocumentation(ItemStack stack) {
        if (stack != null && containsStack(stack)) {
            String returnedKey = getKeyForStack(stack);

            if (returnedKey == null || returnedKey.isEmpty()) {
                return false;
            }

            return documentation.containsKey(returnedKey);
        }

        return false;
    }

}
