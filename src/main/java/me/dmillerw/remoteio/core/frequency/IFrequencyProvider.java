package me.dmillerw.remoteio.core.frequency;

import net.minecraft.util.math.BlockPos;

/**
 * Created by dmillerw
 */
public interface IFrequencyProvider {

    int getFrequency();
    void setFrequency(int frequency);
    BlockPos getPosition();
}
