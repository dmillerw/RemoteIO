package dmillerw.remoteio.recipe;

import dmillerw.remoteio.lib.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class RecipeInhibitorApply implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        int itemCount = 0;
        int inhibitorCount = 0;

        for (int i=0; i<inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
            if (itemStack != null) {
                itemCount++;
                if (itemStack.getItem() == ModItems.interactionInhibitor) {
                    inhibitorCount++;

                }
            }
        }

        return itemCount == 2 && inhibitorCount == 1;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack inhibitor = null;
        ItemStack applyStack = null;

        for (int i=0; i<inventoryCrafting.getSizeInventory(); i++) {
            ItemStack itemStack = inventoryCrafting.getStackInSlot(i);
            if (itemStack != null) {
                if (itemStack.getItem() == ModItems.interactionInhibitor) {
                    inhibitor = itemStack.copy();
                } else {
                    applyStack = itemStack.copy();
                }
            }
        }

        NBTTagCompound nbtTagCompound = applyStack.hasTagCompound() ? applyStack.getTagCompound() : new NBTTagCompound();
        if (inhibitor.getItemDamage() == 0 || inhibitor.getItemDamage() == 1) {
            nbtTagCompound.setByte("inhibit", (byte) 0);
        } else {
            nbtTagCompound.setByte("inhibit", (byte) 1);
        }
        applyStack.setTagCompound(nbtTagCompound);

        return applyStack;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
