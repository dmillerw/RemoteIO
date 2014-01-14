package com.dmillerw.remoteIO.turtle;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

import com.dmillerw.remoteIO.block.tile.TileTurtleBridge;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;
import dan200.turtle.api.ITurtleAccess;

public class TurtleBridgePeripheral implements IHostedPeripheral {

    private final ITurtleAccess turtle;
    
    public TurtleBridgePeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }
    
    @Override
    public String getType() {
        return "turtle_bridge_attached";
    }

    @Override
    public String[] getMethodNames() {
        return new String[] {"sync"};
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method) {
            case 0: {
                Vec3 turtlePos = this.turtle.getPosition();
                TileTurtleBridge tile = (TileTurtleBridge) this.turtle.getWorld().getBlockTileEntity((int)turtlePos.xCoord, (int)turtlePos.yCoord - 1, (int)turtlePos.zCoord);
            
                if (tile != null) {
                    tile.setTurtleID(computer.getID());
                    return new Object[] {true};
                }
                
                return new Object[] {false};
            }
        }
        
        return new Object[] {"Ruh-roh Shaggy"};
    }

    @Override
    public boolean canAttachToSide(int side) {
        return true;
    }

    @Override
    public void attach(IComputerAccess computer) {
        TurtleTracker.INSTANCE.startTracking(computer.getID(), turtle);
    }

    @Override
    public void detach(IComputerAccess computer) {
        TurtleTracker.INSTANCE.stopTracking(computer.getID());
    }

    @Override
    public void update() {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {

    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {

    }

}