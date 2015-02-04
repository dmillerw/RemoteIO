package remoteio.common.inventory.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class SlotPhantomBlock extends SlotPhantom {

    public SlotPhantomBlock(IInventory inventory1, int id, int x, int y) {
        super(inventory1, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() instanceof ItemBlock;
    }
}
