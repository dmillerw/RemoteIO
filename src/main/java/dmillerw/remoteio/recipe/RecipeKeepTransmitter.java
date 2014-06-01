package dmillerw.remoteio.recipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.item.HandlerItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class RecipeKeepTransmitter {

	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting.getItem() == Item.getItemFromBlock(HandlerBlock.remoteInventory)) {
			for (int i=0; i<event.craftMatrix.getSizeInventory(); i++) {
				ItemStack stack = event.craftMatrix.getStackInSlot(i);

				if (stack != null) {
					if (stack.getItem() == HandlerItem.wirelessTransmitter) {
						stack.stackSize++;
					}
				}
			}
		}
	}
}
