package me.dmillerw.remoteio.core.frequency;

import net.minecraft.util.math.BlockPos;

/**
 * Created by dmillerw
 */
public interface IFrequencyProvider {

    public int getFrequency();
    public void setFrequency(int frequency);
    public BlockPos getPosition();
}
