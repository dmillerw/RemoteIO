package com.dmillerw.remoteIO.core.tracker;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.core.helper.IOLogger;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class BlockTracker implements ITickHandler {

	private static BlockTracker instance;
	
	public static BlockTracker getInstance() {
		if (instance == null) {
			instance = new BlockTracker();
		}
		return instance;
	}

	public Map<Integer, List<TrackedBlock>> trackedBlocks = new HashMap<Integer, List<TrackedBlock>>();
	
	public void removeAllWithCallback(ITrackerCallback callback) {
		Iterator<Entry<Integer, List<TrackedBlock>>> iterator = trackedBlocks.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, List<TrackedBlock>> entry = iterator.next();
			Iterator<TrackedBlock> iterator2 = entry.getValue().iterator();
			
			while(iterator2.hasNext()) {
				TrackedBlock tracked = iterator2.next();
				
				if (tracked.callback == callback) {
					tracked.destroy();
				}
			}
		}
	}
	
	public void track(TileEntity tile, ITrackerCallback callback) {
		track(tile.worldObj.provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, callback);
	}
	
	public void track(int dim, int x, int y, int z, ITrackerCallback callback) {
		track(dim, new TrackedBlock(x, y, z, callback));
	}
	
	public void track(int dim, TrackedBlock tracked) {
		if (!trackedBlocks.containsKey(dim) || trackedBlocks.get(dim) == null) {
			trackedBlocks.put(dim, new ArrayList<TrackedBlock>());
		}
		trackedBlocks.get(dim).add(tracked);
	}
	
	public void stopTracking(TileEntity tile) {
		stopTracking(tile.worldObj.provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord);
	}
	
	public void stopTracking(int dim, int x, int y, int z) {
		stopTracking(dim, new TrackedBlock(x, y, z, null));
	}
	
	public void stopTracking(int dim, TrackedBlock tracked) {
		if (!trackedBlocks.containsKey(dim) || trackedBlocks.get(dim) == null) {
			trackedBlocks.put(dim, new ArrayList<TrackedBlock>());
		}
		trackedBlocks.get(dim).remove(tracked);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.WORLD)) {
			World world = (World) tickData[0];
			if (trackedBlocks.containsKey(world.provider.dimensionId)) {
				ListIterator<TrackedBlock> tracked = trackedBlocks.get(world.provider.dimensionId).listIterator();
				while (tracked.hasNext()) {
					TrackedBlock trackedBlock = tracked.next();
					if (trackedBlock.stopTracking) {
						tracked.remove();
					} else {
						trackedBlock.update(world);
					}
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Remote IO Block Tracker";
	}
	
	public static class TrackedBlock {
		public int x;
		public int y;
		public int z;
		
		public int blockID;
		public int blockMeta;
		
		private boolean stopTracking = false;
		
		public BlockState state;
		
		public ITrackerCallback callback;
		
		public TrackedBlock(int x, int y, int z, ITrackerCallback callback) {
			this.x = x;
			this.y = y;
			this.z = z;
			
			if (callback == null) {
				IOLogger.warn("Block tracker was registered without a callback handler. Will be deleted next tick.");
				destroy();
			} else {
				this.callback = callback;
			}
		}
		
		public void update(World world) {
			int id = world.getBlockId(x, y, z);
			
			if (id == 0) {
				state = BlockState.REMOVED;
				if (callback != null) {
					callback.onBlockChanged(this);
				} else {
					destroy();
				}
				return;
			}
			
			int meta = world.getBlockMetadata(x, y, z);
			
			if (id != blockID || meta != blockMeta) {
				state = BlockState.CHANGED;
				blockID = id;
				blockMeta = meta;
				if (callback != null) {
					callback.onBlockChanged(this);
				} else {
					destroy();
				}
				return;
			}
			
			state = BlockState.NORMAL;
		}
		
		public void destroy() {
			this.stopTracking = true;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TrackedBlock) {
				TrackedBlock tracked = (TrackedBlock) obj;
				return tracked.x == this.x && tracked.y == this.y && tracked.z == this.z;
			}
			return false;
		}
		
		@Override
		public String toString() {
			return ("BLOCK " + blockID + ":" + blockMeta + " [" + x + ", " + y + ", " + z + "]");
		}
	}
	
	public static interface ITrackerCallback {
		public void onBlockChanged(TrackedBlock block);
	}
	
	public static enum BlockState {
		NORMAL, CHANGED, REMOVED;
	}
	
}
