package dmillerw.remoteio.recipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dmillerw.remoteio.item.ItemWirelessTransmitter;
import dmillerw.remoteio.lib.ModBlocks;
import dmillerw.remoteio.lib.ModItems;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class RecipeRemoteInventory implements IRecipe {

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() == Item.getItemFromBlock(ModBlocks.remoteInventory)) {
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
                ItemStack stack = event.craftMatrix.getStackInSlot(i);

                if (stack != null) {
                    if (stack.getItem() == ModItems.wirelessTransmitter) {
                        stack.stackSize++;
                    }
                }
            }
        }
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        int interfacesFound = 0;
        int transmittersFound = 0;

        for (int i=0; i<inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
            if (itemStack != null) {
                if (Block.getBlockFromItem(itemStack.getItem()) == ModBlocks.remoteInterface) {
                    interfacesFound++;
                } else if (itemStack.getItem() == ModItems.wirelessTransmitter) {
                    if (ItemWirelessTransmitter.getPlayerName(itemStack) != null) {
                        transmittersFound++;
                    }
                } else {
                    return false;
                }
            }
        }

        return interfacesFound == 1 && transmittersFound == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        for (int i=0; i<inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
            if (itemStack != null) {
                if (itemStack.getItem() == ModItems.wirelessTransmitter) {
                    ItemStack itemStack1 = new ItemStack(ModBlocks.remoteInventory);
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();
                    nbtTagCompound.setString("targetPlayer", ItemWirelessTransmitter.getPlayerName(itemStack));
                    itemStack1.setTagCompound(nbtTagCompound);
                    return itemStack1;
                }
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ModBlocks.remoteInventory);
    }
}
