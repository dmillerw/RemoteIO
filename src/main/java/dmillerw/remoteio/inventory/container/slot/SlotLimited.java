package dmillerw.remoteio.inventory.container.slot;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class SlotLimited extends Slot {

	private final Class limiter;

	public SlotLimited(IInventory inventory1, int id, int x, int y, Class limiter) {
		super(inventory1, id, x, y);

		this.limiter = limiter;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (limiter == null) {
			return true;
		}

		if (stack.getItem() instanceof ItemBlock) {
			return limiter.isInstance(Block.getBlockFromItem(stack.getItem()));
		} else {
			return limiter.isInstance(stack.getItem());
		}
	}
}
