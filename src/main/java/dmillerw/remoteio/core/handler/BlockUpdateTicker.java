package dmillerw.remoteio.core.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import dmillerw.remoteio.block.BlockSkylight;
import dmillerw.remoteio.block.HandlerBlock;
import net.minecraft.block.Block;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Set;

/**
 * @author dmillerw
 */
public class BlockUpdateTicker {

    public static final int MAX_PER_TICK = 10;

    public static final class BlockUpdate {
        public final int x;
        public final int y;
        public final int z;
        public final int dimension;

        public final Block block;
        public final int meta;

        public BlockUpdate(int x, int y, int z, int dimension, Block block, int meta) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.block = block;
            this.meta = meta;
        }

        public void apply(World world) {
            if (world.getBlock(x, y, z) == block) {
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            } else {
                world.setBlock(x, y, z, block, meta, 3);
            }
            world.updateLightByType(EnumSkyBlock.Block, x, y, z);
            world.updateLightByType(EnumSkyBlock.Sky, x, y, z);
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                Block block = world.getBlock(x + side.offsetX, y + side.offsetY, z + side.offsetZ);
                if (block != null && block == HandlerBlock.skylight) {
                    ((BlockSkylight)block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, HandlerBlock.skylight, meta);
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BlockUpdate that = (BlockUpdate) o;

            if (dimension != that.dimension) return false;
            if (x != that.x) return false;
            if (y != that.y) return false;
            if (z != that.z) return false;

            return true;
        }
        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            result = 31 * result + dimension;
            return result;
        }
    }

    public static void registerBlockUpdate(World world, int x, int y, int z, Block block, int meta) {
        blockUpdateSet.add(new BlockUpdate(x, y, z, world.provider.dimensionId, block, meta));
    }

    private static Set<BlockUpdate> blockUpdateSet = Sets.newConcurrentHashSet();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        List<BlockUpdate> removalList = Lists.newArrayList();
        int updateCount = 0;
        for (BlockUpdate blockUpdate : blockUpdateSet) {
            if (updateCount >= MAX_PER_TICK) break;
            if (event.world.provider.dimensionId == blockUpdate.dimension) {
                blockUpdate.apply(event.world);
                removalList.add(blockUpdate);
                updateCount++;
            }
        }
        for (int i=0; i<removalList.size(); i++) {
            blockUpdateSet.remove(removalList.get(i));
        }
    }
}
