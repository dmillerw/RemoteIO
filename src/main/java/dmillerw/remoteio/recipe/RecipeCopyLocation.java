package dmillerw.remoteio.recipe;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dmillerw.remoteio.item.HandlerItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class RecipeCopyLocation implements IRecipe {

    public static final RecipeCopyLocation INSTANCE = new RecipeCopyLocation();

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() == HandlerItem.locationChip && event.crafting.hasTagCompound()) {
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
                ItemStack stack = event.craftMatrix.getStackInSlot(i);

                if (stack != null) {
                    if (stack.getItem() == HandlerItem.locationChip) {
                        if (stack.hasTagCompound()) {
                            stack.stackSize++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int foundBlank = 0;
        int foundFilled = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack != null) {
                if (stack.getItem() == HandlerItem.locationChip) {
                    if (stack.hasTagCompound()) {
                        foundFilled++;
                    } else {
                        foundBlank++;
                    }
                } else {
                    return false;
                }
            }
        }

        return foundBlank == 1 && foundFilled == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack != null) {
                if (stack.getItem() == HandlerItem.locationChip) {
                    if (stack.hasTagCompound()) {
                        return stack.copy();
                    }
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
        return new ItemStack(HandlerItem.locationChip);
    }
}
