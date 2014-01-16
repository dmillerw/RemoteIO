package com.dmillerw.remoteIO.block.tile;

import com.dmillerw.remoteIO.lib.DimensionalCoords;
import com.dmillerw.remoteIO.turtle.TurtleInventoryWrapper;
import com.dmillerw.remoteIO.turtle.TurtleTracker;
import dan200.turtle.api.ITurtleAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileTurtleBridge extends TileIOCore implements IInventory {

    public boolean addedToEnergyNet = false;

    public int turtleID = -1;
    
    public synchronized int getTurtleID() {
        return this.turtleID;
    }
    
    public synchronized void setTurtleID(int id) {
        this.turtleID = id;
        
        if (this.turtleID >= 0 && !lastClientState) {
            lastClientState = true;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        } else if (this.turtleID == -1 && lastClientState) {
            lastClientState = false;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void cleanup() {
        if (addedToEnergyNet) {
            // MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            addedToEnergyNet = false;
        }
    }

    @Override
    public DimensionalCoords connectionPosition() {
        ITurtleAccess turtle = getTurtle();

        if (turtle == null) {
            return null;
        }

        return DimensionalCoords.fromTurtle(turtle);
    }

    @Override
    public Object getLinkedObject() {
        return getTurtleInventory();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (!addedToEnergyNet) {
//              MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
                addedToEnergyNet = true;
            }
            
            if (worldObj.getTotalWorldTime() % 10 == 0) {
                getTurtleInventoryWithUpdate();
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);

        nbt.setInteger("turtleID", turtleID);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);

        turtleID = nbt.getInteger("turtleID");
    }

    private ITurtleAccess getTurtle() {
        if (this.turtleID < 0) {
            return null;
        }

        ITurtleAccess access = TurtleTracker.INSTANCE.getTurtleForID(this.turtleID);

        if (access == null) {
            return null;
        }

        if (!inRange()) {
            return null;
        }

        return access;
    }

    private ITurtleAccess getTurtleWithUpdate() {
        update();
        return getTurtle();
    }

    private IInventory getTurtleInventory() {
        ITurtleAccess access = getTurtleWithUpdate();
        
        if (access == null) {
            return null;
        }
        
        // Vec3 tileCoords = Vec3.fakePool.getVecFromPool(xCoord, yCoord, zCoord);
        
        // if (tileCoords.distanceTo(access.getPosition()) > this.getRange()) {
        //     return null;
        // }
        
        return new TurtleInventoryWrapper(access);
    }
    
    private IInventory getTurtleInventoryWithUpdate() {
        update();
        return getTurtleInventory();
    }
    
    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        return getTurtleInventoryWithUpdate() != null ? getTurtleInventoryWithUpdate().getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return getTurtleInventoryWithUpdate() != null ? getTurtleInventoryWithUpdate().getStackInSlot(i) : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return getTurtleInventoryWithUpdate() != null ? getTurtleInventoryWithUpdate().decrStackSize(i, j) : null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return getTurtleInventoryWithUpdate() != null ? getTurtleInventoryWithUpdate().getStackInSlotOnClosing(i) : null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (getTurtleInventoryWithUpdate() != null) getTurtleInventoryWithUpdate().setInventorySlotContents(i, itemstack);
    }

    @Override
    public String getInvName() {
        return getTurtleInventoryWithUpdate() != null ? getTurtleInventoryWithUpdate().getInvName() : "";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openChest() {
        
    }

    @Override
    public void closeChest() {
        
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

}
