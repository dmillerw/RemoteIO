package me.dmillerw.remoteio.core.frequency;

import com.google.common.collect.Maps;
import me.dmillerw.remoteio.tile.TileAnalyzer;
import net.minecraft.util.math.BlockPos;

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

    public static BlockPos getWatchedBlock(int frequency) {
        if (!isFrequencyTaken(frequency))
            return null;

        TileAnalyzer tile = registeredAnalyzers.get(frequency);
        if (tile == null)
            return null;

        return tile.getWatchedPosition();
    }
}
