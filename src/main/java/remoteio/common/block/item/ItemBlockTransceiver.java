package remoteio.common.block.item;

import remoteio.common.tile.TileTransceiver;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public class ItemBlockTransceiver extends ItemBlock {

    public ItemBlockTransceiver(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean result = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);

        if (result) {
            TileTransceiver tileTransceiver = (TileTransceiver) world.getTileEntity(x, y, z);
            tileTransceiver.orientation = ForgeDirection.getOrientation(side).getOpposite();
            tileTransceiver.markForUpdate();
        }

        return result;
    }
}
