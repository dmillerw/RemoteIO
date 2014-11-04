package dmillerw.remoteio.core.compat;

import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.lib.VisualState;
import dmillerw.remoteio.tile.TileRemoteInterface;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * @author dmillerw
 */
public class WailaProvider implements IWailaDataProvider {

    public static void registerProvider(IWailaRegistrar wailaRegistrar) {
        wailaRegistrar.registerStackProvider(new WailaProvider(), BlockRemoteInterface.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity != null && tileEntity instanceof TileRemoteInterface) {
            TileRemoteInterface tileRemoteInterface = (TileRemoteInterface) tileEntity;


            if (tileRemoteInterface.visualState == VisualState.CAMOUFLAGE_REMOTE) {
                if (tileRemoteInterface.remotePosition != null && tileRemoteInterface.remotePosition.inWorld(accessor.getWorld())) {
                    return new ItemStack(tileRemoteInterface.remotePosition.getBlock(), 1, tileRemoteInterface.remotePosition.getMeta());
                }
            }
        }
        return accessor.getStack();
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }
}
