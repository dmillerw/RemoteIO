package com.dmillerw.remoteIO.block.tile;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Dylan Miller on 1/14/14
 */
public class FuelHandler {

    public static FuelHandler fromNBT(NBTTagCompound nbt) {
        return new FuelHandler(nbt.getInteger("capacity"), nbt.getInteger("fuelLevel"));
    }

    private final int capacity;

    public int fuelLevel;

    public FuelHandler(int capacity) {
        this(capacity, 0);
    }

    public FuelHandler(int capacity, int fuelLevel) {
        this.capacity = capacity;
        this.fuelLevel = fuelLevel;
    }

    public boolean addFuel(int value) {
        if (this.capacity > 0 && this.fuelLevel + value >= capacity) {
            return false;
        }

        fuelLevel += value;
        return true;
    }

    public boolean consumeFuel(int value) {
        if (this.fuelLevel - value < 0) {
            return false;
        }

        fuelLevel -= value;
        return true;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("capacity", capacity);
        nbt.setInteger("fuelLevel", fuelLevel);
    }

}
