package dmillerw.remoteio.recipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dmillerw.remoteio.lib.ModBlocks;
import dmillerw.remoteio.lib.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class RecipeKeepTransmitter {

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
}
