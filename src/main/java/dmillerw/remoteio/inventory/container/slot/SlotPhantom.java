package dmillerw.remoteio.inventory.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * @author dmillerw
 */
public class SlotPhantom extends Slot {

    public SlotPhantom(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    public boolean canShift() {
        return true;
    }

    public boolean canAdjust() {
        return true;
    }

    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
