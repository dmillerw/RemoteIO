package dmillerw.remoteio.core.tracker;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public class RedstoneTracker {

    // Key is the REMOTE block position, value is remote interface tile
    private static BiMap<DimensionalCoords, DimensionalCoords> positionBiMap = HashBiMap.create();

    public static void register(TileRemoteInterface tileRemoteInterface) {
        if (tileRemoteInterface.remotePosition != null && tileRemoteInterface.hasTransferChip(TransferType.REDSTONE)) {
            positionBiMap.put(tileRemoteInterface.remotePosition, DimensionalCoords.create(tileRemoteInterface));
            tileRemoteInterface.remotePosition.markForUpdate();
        }
    }

    public static void unregister(TileRemoteInterface tileRemoteInterface) {
        DimensionalCoords remote = positionBiMap.inverse().get(DimensionalCoords.create(tileRemoteInterface));
        if (remote != null) {
            positionBiMap.remove(remote);
            remote.markForUpdate();
        }
    }

    public static boolean isBlockIndirectlyGettingPowered(World world, int x, int y, int z) {
        DimensionalCoords dimensionalCoords = new DimensionalCoords(world, x, y, z);
        DimensionalCoords tile = positionBiMap.get(dimensionalCoords);

        // First check remote redstone levels, if it exists
        if (tile != null) {
            for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
                if (isBlockIndirectlyGettingPowered_default(tile.getWorld(), tile.x + forgeDirection.offsetX, tile.y + forgeDirection.offsetY, tile.z + forgeDirection.offsetZ)) {
                    return true;
                }
            }
        }
        
        // Then check self, if remote doesn't return
        for (ForgeDirection forgeDirection : ForgeDirection.VALID_DIRECTIONS) {
            if (isBlockIndirectlyGettingPowered_default(world, x + forgeDirection.offsetX, y + forgeDirection.offsetY, z + forgeDirection.offsetZ)) {
                return true;
            }
        }

        return false;
    }
    
    private static boolean isBlockIndirectlyGettingPowered_default(World world, int x, int y, int z) {
        return (world.getIndirectPowerLevelTo(x, y - 1, z, 0) > 0) || ((world.getIndirectPowerLevelTo(x, y + 1, z, 1) > 0) || ((world.getIndirectPowerLevelTo(x, y, z - 1, 2) > 0) || ((world.getIndirectPowerLevelTo(x, y, z + 1, 3) > 0) || ((world.getIndirectPowerLevelTo(x - 1, y, z, 4) > 0) || (world.getIndirectPowerLevelTo(x + 1, y, z, 5) > 0)))));
    }
}
