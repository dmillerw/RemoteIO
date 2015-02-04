package remoteio.common.inventory.container;

import com.google.common.collect.Lists;
import remoteio.common.inventory.InventoryTileCrafting;
import remoteio.common.network.PacketHandler;
import remoteio.common.network.packet.PacketClientForceSlot;
import remoteio.common.tile.TileIntelligentWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author dmillerw
 */
public class ContainerIntelligentWorkbench extends Container {

    public static List<ItemStack> getAllCraftingResults(InventoryTileCrafting craftMatrix, World world) {
        List<ItemStack> list = Lists.newArrayList();

        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < craftMatrix.getSizeInventory(); ++j) {
            ItemStack itemstack2 = craftMatrix.getStackInSlot(j);

            if (itemstack2 != null) {
                if (i == 0) {
                    itemstack = itemstack2;
                }

                if (i == 1) {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable()) {
            Item item = itemstack.getItem();
            int j1 = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int k = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;

            if (i1 < 0) {
                i1 = 0;
            }

            list.add(new ItemStack(itemstack.getItem(), 1, i1));
        } else {
            for (j = 0; j < CraftingManager.getInstance().getRecipeList().size(); ++j) {
                IRecipe irecipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(j);

                if (irecipe.matches(craftMatrix, world)) {
                    list.add(irecipe.getCraftingResult(craftMatrix));
                }
            }
        }

        return list;
    }

    public final TileIntelligentWorkbench tileIntelligentWorkbench;
    public IInventory craftResult = new InventoryCraftResult();

    public int resultCount = 0;
    public boolean resultChanged = false;
    private int recipeIndex = 0;

    private List<ItemStack> results;

    public ContainerIntelligentWorkbench(InventoryPlayer inventoryPlayer, World world, int x, int y, int z) {
        this.tileIntelligentWorkbench = (TileIntelligentWorkbench) world.getTileEntity(x, y, z);
        this.tileIntelligentWorkbench.craftMatrix.setContainer(this);
        this.addSlotToContainer(new SlotCrafting(inventoryPlayer.player, tileIntelligentWorkbench.craftMatrix, this.craftResult, 0, 124, 35));
        int l;
        int i1;

        for (l = 0; l < 3; ++l) {
            for (i1 = 0; i1 < 3; ++i1) {
                this.addSlotToContainer(new Slot(tileIntelligentWorkbench.craftMatrix, i1 + l * 3, 30 + i1 * 18, 17 + l * 18));
            }
        }

        for (l = 0; l < 3; ++l) {
            for (i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(inventoryPlayer, i1 + l * 9 + 9, 8 + i1 * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(inventoryPlayer, l, 8 + l * 18, 142));
        }

        this.onCraftMatrixChanged(tileIntelligentWorkbench.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        results = ContainerIntelligentWorkbench.getAllCraftingResults(tileIntelligentWorkbench.craftMatrix, tileIntelligentWorkbench.getWorldObj());
        recipeIndex = 0;
        this.craftResult.setInventorySlotContents(0, !results.isEmpty() ? results.get(recipeIndex) : null);
        detectAndSendChanges();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (resultCount != results.size()) {
            for (Object crafter : this.crafters) {
                ((ICrafting) crafter).sendProgressBarUpdate(this, 0, results.size());
            }
            resultCount = results.size();
        }
    }

    @Override
    public void updateProgressBar(int id, int value) {
        super.updateProgressBar(id, value);
        switch (id) {
            case 0:
                resultCount = value;
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int data) {
        if (data == 1) {
            recipeIndex++;
            if (recipeIndex >= resultCount) {
                recipeIndex = 0;
            }
        } else if (data == -1) {
            recipeIndex--;
            if (recipeIndex < 0) {
                recipeIndex = resultCount - 1;
            }
        }

        this.craftResult.setInventorySlotContents(0, !results.isEmpty() ? results.get(recipeIndex) : null);
        PacketHandler.INSTANCE.sendTo(new PacketClientForceSlot(0, this.craftResult.getStackInSlot(0)), (EntityPlayerMP) player);

        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq((double) tileIntelligentWorkbench.xCoord + 0.5D, (double) tileIntelligentWorkbench.yCoord + 0.5D, (double) tileIntelligentWorkbench.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotNumber);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (slotNumber == 0) {
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (slotNumber >= 10 && slotNumber < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return null;
                }
            } else if (slotNumber >= 37 && slotNumber < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean func_94530_a(ItemStack stack, Slot slot) {
        return slot.inventory != this.craftResult && super.func_94530_a(stack, slot);
    }
}
