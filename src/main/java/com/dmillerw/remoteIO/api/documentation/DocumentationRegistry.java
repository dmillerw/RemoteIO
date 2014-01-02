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

    private static Map<ItemStack, String> stackToKeyMapping = new HashMap<ItemStack, String>();
    private static Map<String, String> documentation = new HashMap<String, String>();

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

    public static void addDocumentation(String key, String[] docu) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<docu.length; i++) {
            sb.append(docu[i]);
            if (i != docu.length - 1) {
                sb.append("\n");
            }
        }
        addDocumentation(key, sb.toString());
    }

    public static void addDocumentation(String key, String docu) {
        docu = docu.replace("\n", "\n\n");

        if (!documentation.containsKey(key)) {
            documentation.put(key, docu);
        } else {
            throw new RuntimeException("[RemoteIO] Something tried to register documentation with the key " + key + " but that key is already registsered!");
        }
    }

    public static String getDocumentation(ItemStack stack) {
        if (hasDocumentation(stack)) {
            return getDocumentation(getKeyForStack(stack));
        }

        return "";
    }

    private static String getDocumentation(String key) {
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
