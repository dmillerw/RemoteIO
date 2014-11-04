package dmillerw.remoteio.core.tracker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author dmillerw
 */
public class BlockTracker {

    public static final BlockTracker INSTANCE = new BlockTracker();

    private Set<TrackedBlock> trackedBlockSet = new HashSet<TrackedBlock>();

    public void startTracking(DimensionalCoords coords, ITrackerCallback callback) {
        if (coords == null || callback == null) {
            return;
        }

        trackedBlockSet.add(new TrackedBlock(coords, callback));
    }

    public void stopTracking(DimensionalCoords coords) {
        if (coords == null) {
            return;
        }

        for (TrackedBlock trackedBlock : trackedBlockSet) {
            if (trackedBlock.coordinates.equals(coords)) {
                trackedBlock.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent event) {
        if (event.side == Side.SERVER && event.type == TickEvent.Type.WORLD && event.phase == TickEvent.Phase.END) {
            Iterator<TrackedBlock> iterator = trackedBlockSet.iterator();

            while (iterator.hasNext()) {
                TrackedBlock trackedBlock = iterator.next();

                if (trackedBlock.isDead) {
                    iterator.remove();
                } else {
                    if (trackedBlock.coordinates.getBlock() != trackedBlock.lastBlock || trackedBlock.coordinates.getMeta() != trackedBlock.lastMeta) {
                        trackedBlock.callback();
                        trackedBlock.lastBlock = trackedBlock.coordinates.getBlock();
                        trackedBlock.lastMeta = trackedBlock.coordinates.getMeta();
                    } else {
                        Block block = trackedBlock.coordinates.getBlock();

                        for (int i=0; i<6; i++) {
                            int comparator = block.getComparatorInputOverride(trackedBlock.coordinates.getWorld(), trackedBlock.coordinates.x, trackedBlock.coordinates.y, trackedBlock.coordinates.z, i);
                            if (comparator != trackedBlock.lastComparatorValue) {
                                trackedBlock.callback();
                                trackedBlock.lastComparatorValue = comparator;
                                break;
                            }
                        }

                        for (int i=0; i<6; i++) {
                            int redstone = block.isProvidingWeakPower(trackedBlock.coordinates.getWorld(), trackedBlock.coordinates.x, trackedBlock.coordinates.y, trackedBlock.coordinates.z, i);
                            if (redstone != trackedBlock.lastWeakRedstoneValue) {
                                trackedBlock.callback();
                                trackedBlock.lastWeakRedstoneValue = redstone;
                            }
                        }

                        for (int i=0; i<6; i++) {
                            int redstone = block.isProvidingStrongPower(trackedBlock.coordinates.getWorld(), trackedBlock.coordinates.x, trackedBlock.coordinates.y, trackedBlock.coordinates.z, i);
                            if (redstone != trackedBlock.lastStrongRedstoneValue) {
                                trackedBlock.callback();
                                trackedBlock.lastStrongRedstoneValue = redstone;
                            }
                        }
                    }
                }
            }
        }
    }

    public static class TrackedBlock {
        public final DimensionalCoords coordinates;
        public Block lastBlock;
        public int lastMeta;
        public int lastComparatorValue;
        public int lastWeakRedstoneValue;
        public int lastStrongRedstoneValue;
        public final ITrackerCallback callback;
        public boolean isDead = false;

        public TrackedBlock(DimensionalCoords coordinates, ITrackerCallback callback) {
            this.coordinates = coordinates;
            this.callback = callback;
        }

        public void callback() {
            callback.callback(coordinates.getWorld(), coordinates.x, coordinates.y, coordinates.z);
        }

        public void setDead() {
            isDead = true;
        }

        @Override
        public int hashCode() {
            return coordinates.hashCode();
        }
    }

    public static interface ITrackerCallback {
        public void callback(IBlockAccess world, int x, int y, int z);
    }
}
