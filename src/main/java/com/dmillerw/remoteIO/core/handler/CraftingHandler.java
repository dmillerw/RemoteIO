package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.item.ItemHandler;
import cpw.mods.fml.common.ICraftingHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created by Dylan Miller on 1/16/14
 */
public class CraftingHandler implements ICraftingHandler {

    @Override
    public void onCrafting(EntityPlayer entityPlayer, ItemStack itemStack, IInventory inventory) {
        if (itemStack.itemID == BlockHandler.blockWireless.blockID) {
            for (int i=0; i<inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);

                if (stack != null) {
                    if (stack.itemID == ItemHandler.itemTransmitter.itemID) {
                        stack.stackSize++;
                    }
                }
            }
        }
    }

    @Override
    public void onSmelting(EntityPlayer entityPlayer, ItemStack itemStack) {

    }

}
