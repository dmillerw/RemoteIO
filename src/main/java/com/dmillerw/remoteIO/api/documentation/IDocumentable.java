package com.dmillerw.remoteIO.api.documentation;

import net.minecraft.item.ItemStack;

/**
 * Created by Dylan Miller on 1/1/14
 */
public interface IDocumentable {

    /**
     * Get the documentation identifier String for this ItemStack. Can return null or empty,
     * in which case the item will be rejected. The item will also be rejected if no
     * documentation for the returned key can be found.
     * @param stack ItemStack placed in the documentation GUI slot
     * @return Unique key used to return proper documentation. Can be null or empty.
     */
    public String getKey(ItemStack stack);

}
