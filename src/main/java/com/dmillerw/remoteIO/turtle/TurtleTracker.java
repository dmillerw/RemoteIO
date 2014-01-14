package com.dmillerw.remoteIO.turtle;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.core.tracker.BlockTracker.ITrackerCallback;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import dan200.computer.api.IComputerAccess;
import dan200.turtle.api.ITurtleAccess;

public class TurtleTracker {

    public static final TurtleTracker INSTANCE = new TurtleTracker();
    
    public BiMap<Integer, ITurtleAccess> turtleMapping = HashBiMap.<Integer, ITurtleAccess>create();
    
    public void startTracking(int id, ITurtleAccess turtle) {
        synchronized (turtleMapping) {
            turtleMapping.put(id, turtle);
        }
    }
    
    public void stopTracking(int id) {
        synchronized (turtleMapping) {
            turtleMapping.remove(id);
        }
    }
    
    public synchronized ITurtleAccess getTurtle(IComputerAccess computer) {
        return getTurtleForID(computer.getID());
    }
    
    public ITurtleAccess getTurtleForID(int id) {
        synchronized (turtleMapping) {
            return turtleMapping.get(id);
        }
    }

}
