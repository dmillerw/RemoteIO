package me.dmillerw.remoteio.tile;

import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Created by dmillerw
 */
public class TileAnalyzer extends TileCore implements IFrequencyProvider {

    private int frequency = 0;

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        compound.setInteger("_frequency", frequency);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        frequency = compound.getInteger("_frequency");
    }

    //TODO: Sync for GUI, some other way than through the description packet
    @Override
    public void writeDescription(NBTTagCompound compound) {
        compound.setInteger("_frequency", frequency);
    }

    @Override
    public void readDescription(NBTTagCompound compound) {
        frequency = compound.getInteger("_frequency");
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            DeviceRegistry.registerAnalyzer(this);
        }
    }

    @Override
    public final int getFrequency() {
        return frequency;
    }

    @Override
    public final void setFrequency(int frequency) {
        DeviceRegistry.unregisterAnalyzer(this);
        this.frequency = frequency;
        DeviceRegistry.registerAnalyzer(this);

        markDirtyAndNotify();
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    public final BlockPos getWatchedPosition() {
        return pos.add(0, 1, 0).toImmutable();
    }
}
