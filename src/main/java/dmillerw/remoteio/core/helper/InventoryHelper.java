package dmillerw.remoteio.core.helper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * @author dmillerw
 */
public class InventoryHelper {

    private static Random random = new Random();

    public static ItemStack[] toArray(IInventory inventory) {
        ItemStack[] items = new ItemStack[inventory.getSizeInventory()];
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null) {
                items[i] = stack;
            }
        }
        return items;
    }

    public static boolean containsStack(IInventory inventory, ItemStack stack, boolean compareMeta, boolean compareNBT) {
        ItemStack[] items = toArray(inventory);

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if (item != null) {
                if (stack.getItem() == item.getItem()) {
                    if (!compareMeta || (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE || item.getItemDamage() == OreDictionary.WILDCARD_VALUE) || stack.getItemDamage() == item.getItemDamage()) {
                        if (!compareNBT || (ItemStack.areItemStacksEqual(stack, item))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static void dropContents(IInventory inventory, World world, int x, int y, int z) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);

            if (stack != null) {
                float f = random.nextFloat() * 0.8F + 0.1F;
                float f1 = random.nextFloat() * 0.8F + 0.1F;
                float f2 = random.nextFloat() * 0.8F + 0.1F;

                while (stack.stackSize > 0) {
                    int j1 = random.nextInt(21) + 10;

                    if (j1 > stack.stackSize) {
                        j1 = stack.stackSize;
                    }

                    stack.stackSize -= j1;
                    EntityItem entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2), new ItemStack(stack.getItem(), j1, stack.getItemDamage()));

                    if (stack.hasTagCompound()) {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                    }

                    float f3 = 0.05F;
                    entityitem.motionX = (double) ((float) random.nextGaussian() * f3);
                    entityitem.motionY = (double) ((float) random.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double) ((float) random.nextGaussian() * f3);

                    world.spawnEntityInWorld(entityitem);
                }
            }
        }
    }
}
