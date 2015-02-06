package remoteio.common.core.compat;

import remoteio.common.block.BlockRemoteInterface;
import remoteio.common.block.BlockRemoteInventory;
import remoteio.common.lib.VisualState;
import remoteio.common.tile.TileRemoteInterface;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import remoteio.common.tile.TileRemoteInventory;

import java.util.List;

/**
 * @author dmillerw
 */
public class WailaProvider implements IWailaDataProvider {

    public static void registerProvider(IWailaRegistrar wailaRegistrar) {
        WailaProvider provider = new WailaProvider();
        wailaRegistrar.registerStackProvider(provider, BlockRemoteInterface.class);
        wailaRegistrar.registerBodyProvider(provider, BlockRemoteInterface.class);
        wailaRegistrar.registerBodyProvider(provider, BlockRemoteInventory.class);
        wailaRegistrar.registerTailProvider(provider, BlockRemoteInventory.class);
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
        TileEntity tile = accessor.getTileEntity();
        if(tile != null){
            if(tile instanceof TileRemoteInterface){
                TileRemoteInterface remoteInterface = (TileRemoteInterface) tile;
                if(!remoteInterface.visualState.isCamouflage()){
                    currenttip.add(remoteInterface.visualState.toString());
                }
            } else if(tile instanceof TileRemoteInventory){
                TileRemoteInventory remoteInventory = (TileRemoteInventory) tile;
                if(!remoteInventory.visualState.isCamouflage()){
                    currenttip.add(remoteInventory.visualState.toString());
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if(tile != null){
            if(tile instanceof TileRemoteInventory){
                TileRemoteInventory remoteInventory = (TileRemoteInventory) tile;
                if(!remoteInventory.visualState.isCamouflage() && remoteInventory.getPlayer() != null){
                    currenttip.add("Bound To: " + remoteInventory.getPlayer().getDisplayName());
                }
            }
        }
        return currenttip;
    }
}
