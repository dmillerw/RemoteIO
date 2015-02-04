package remoteio.common.core.tracker;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import remoteio.common.core.TransferType;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.tile.TileRemoteInterface;
import net.minecraft.block.Block;
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

    public static int getIndirectPowerLevelTo(World world, int x, int y, int z, int side) {
        ForgeDirection forgeDirection = ForgeDirection.getOrientation(side).getOpposite();
        int fx = x + forgeDirection.offsetX;
        int fy = y + forgeDirection.offsetY;
        int fz = z + forgeDirection.offsetZ;

        DimensionalCoords dimensionalCoords = new DimensionalCoords(world, fx, fy, fz);
        DimensionalCoords tileCoords = positionBiMap.get(dimensionalCoords);

        int level = 0;
        if (tileCoords != null) {
            level = default_getIndirectPowerLevelTo(tileCoords.getWorld(), tileCoords.x + forgeDirection.getOpposite().offsetX, tileCoords.y + forgeDirection.getOpposite().offsetY, tileCoords.z + forgeDirection.getOpposite().offsetZ, side);
        }

        int local = default_getIndirectPowerLevelTo(world, x, y, z, side);
        if (local > level)
            level = local;

        return level;
    }

    private static int default_getIndirectPowerLevelTo(World world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        return block.shouldCheckWeakPower(world, x, y, z, side) ? world.getBlockPowerInput(x, y, z) : block.isProvidingWeakPower(world, x, y, z, side);
    }
}
