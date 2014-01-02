package com.dmillerw.remoteIO.api.documentation;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dylan Miller on 1/1/14
 */
public class DocumentationRegistry {

    private static Map<String, String> documentation = new HashMap<String, String>();

    //TODO Add basic formatting abilities

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
            return getDocumentation(((IDocumentable)stack.getItem()).getKey(stack));
        }

        return null;
    }

    private static String getDocumentation(String key) {
        return documentation.get(key);
    }

    public static boolean hasDocumentation(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IDocumentable) {
            String returnedKey = ((IDocumentable)stack.getItem()).getKey(stack);

            if (returnedKey == null || returnedKey.isEmpty()) {
                return false;
            }

            return documentation.containsKey(returnedKey);
        }

        return false;
    }

}
