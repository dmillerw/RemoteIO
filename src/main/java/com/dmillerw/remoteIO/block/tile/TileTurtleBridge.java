package com.dmillerw.remoteIO.block.tile;

import cofh.api.energy.IEnergyHandler;
import com.dmillerw.remoteIO.core.helper.EnergyHelper;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.lib.DimensionalCoords;
import com.dmillerw.remoteIO.turtle.TurtleInventoryWrapper;
import com.dmillerw.remoteIO.turtle.TurtleTracker;
import dan200.turtle.api.ITurtleAccess;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileTurtleBridge extends TileIOCore implements IInventory, IEnergySink, IEnergyHandler {

    public boolean addedToEnergyNet = false;

    public int turtleID = -1;
    
    public synchronized int getTurtleID() {
        return this.turtleID;
    }
    
    public synchronized void setTurtleID(int id) {
        this.turtleID = id;
        
        if (this.turtleID >= 0 && !lastClientState) {
            lastClientState = true;
            update();
        } else if (this.turtleID == -1 && lastClientState) {
            lastClientState = false;
            update();
        }
    }

    @Override
    public void cleanup() {
        if (addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            addedToEnergyNet = false;
        }
    }

    @Override
    public DimensionalCoords connectionPosition() {
        ITurtleAccess turtle = TurtleTracker.INSTANCE.getTurtleForID(this.turtleID);

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
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
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

    private ITurtleAccess getTurtle(ItemUpgrade.Upgrade upgrade) {
        if (!hasUpgrade(upgrade)) {
            return null;
        }

        return getTurtle();
    }

    private ITurtleAccess getTurtleWithUpdate() {
        update();
        return getTurtle();
    }

    private ITurtleAccess getTurtleWithUpdate(ItemUpgrade.Upgrade upgrade) {
        if (!hasUpgrade(upgrade)) {
            return null;
        }

        return getTurtleWithUpdate();
    }

    private IInventory getTurtleInventory() {
        ITurtleAccess access = getTurtleWithUpdate(ItemUpgrade.Upgrade.ITEM);
        
        if (access == null) {
            return null;
        }
        
        if (!inRange()) {
            return null;
        }

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

    /* IENERGYSINK */
    @Override
    public double demandedEnergyUnits() {
        ITurtleAccess turtle = getTurtleWithUpdate(ItemUpgrade.Upgrade.POWER_EU);
        return turtle != null ? EnergyHelper.requiresCharge(turtle, EnergyHelper.EnergyType.EU) ? 32D : 0D : 0D;
    }

    @Override
    public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
        ITurtleAccess turtle = getTurtleWithUpdate(ItemUpgrade.Upgrade.POWER_EU);
        return turtle != null ? EnergyHelper.distributeCharge(turtle, EnergyHelper.EnergyType.EU, (int)amount, false) : amount;
    }

    @Override
    public int getMaxSafeInput() {
        return Integer.MAX_VALUE; // Maybe temp for now
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return getTurtleWithUpdate(ItemUpgrade.Upgrade.POWER_EU) != null;
    }

    /* IENERGYHANDLER */
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        ITurtleAccess turtle = getTurtleWithUpdate(ItemUpgrade.Upgrade.POWER_RF);
        return turtle != null ? EnergyHelper.distributeCharge(turtle, EnergyHelper.EnergyType.RF, maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canInterface(ForgeDirection from) {
        return getTurtleWithUpdate(ItemUpgrade.Upgrade.POWER_RF) != null;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return 0;
    }
}
