package me.dmillerw.remoteio.core.frequency;

import com.google.common.collect.Maps;
import me.dmillerw.remoteio.tile.TileAnalyzer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by dmillerw
 */
public class DeviceRegistry {

    private static Map<Integer, TileAnalyzer> registeredAnalyzers = Maps.newHashMap();

    public static boolean isFrequencyTaken(int frequency) {
        return registeredAnalyzers.containsKey(frequency);
    }

    public static boolean registerAnalyzer(TileAnalyzer analyzer) {
        if (isFrequencyTaken(analyzer.getFrequency()))
            return false;

        registeredAnalyzers.put(analyzer.getFrequency(), analyzer);

        return true;
    }

    public static void unregisterAnalyzer(TileAnalyzer analyzer) {
        registeredAnalyzers.remove(analyzer.getFrequency());
    }

    // Dimension param is a royal hack
    //TODO: Proper dimension support
    public static BlockPos getWatchedBlock(int dimension, int frequency) {
        if (!isFrequencyTaken(frequency))
            return null;

        TileAnalyzer tile = registeredAnalyzers.get(frequency);
        if (tile == null)
            return null;

        if (tile.getWorld().provider.getDimension() != dimension)
            return null;

        return tile.getWatchedPosition();
    }

    public enum TickHandler {
        INSTANCE;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Iterator<Map.Entry<Integer, TileAnalyzer>> iterator = registeredAnalyzers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, TileAnalyzer> entry = iterator.next();

                    if (entry.getValue() == null || entry.getValue().isInvalid()) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
