package me.dmillerw.remoteio.tile;

import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import net.minecraft.util.math.BlockPos;

/**
 * Created by dmillerw
 */
public class TileAnalyzer extends TileCore {

    private int frequency = 0;

    @Override
    public void onLoad() {
        if (!worldObj.isRemote) {
            DeviceRegistry.registerAnalyzer(this);
        }
    }

    public final int getFrequency() {
        return frequency;
    }

    public final void setFrequency(int frequency, boolean unregister) {
        if (DeviceRegistry.isFrequencyTaken(frequency))
            DeviceRegistry.unregisterAnalyzer(this);

        this.frequency = frequency;

        DeviceRegistry.registerAnalyzer(this);
    }

    public final BlockPos getWatchedPosition() {
        return pos.add(0, 1, 0).toImmutable();
    }
}
