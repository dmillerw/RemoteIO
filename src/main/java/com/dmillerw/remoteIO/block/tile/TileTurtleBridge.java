package com.dmillerw.remoteIO.block.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.turtle.TurtleInventoryWrapper;
import com.dmillerw.remoteIO.turtle.TurtleTracker;

import dan200.turtle.api.ITurtleAccess;

public class TileTurtleBridge extends TileCore implements IInventory {

    public IInventory upgrades = new InventoryBasic("Upgrades", false, 9);
    public IInventory camo = new InventoryBasic("Camo", false, 1) {
        @Override
        public void onInventoryChanged() {
            super.onInventoryChanged();
            if (worldObj != null) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    };

    public boolean addedToEnergyNet = false;

    public boolean unlimitedRange = false;
    public boolean remoteRequired = false;
    
    public boolean lastClientState = false;
    
    public boolean redstoneState = false;
    
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
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (!addedToEnergyNet) {
//              MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
                addedToEnergyNet = true;
            }
            
            if (worldObj.getTotalWorldTime() % 10 == 0) {
                getAndUpdate();
            }
        }
    }

    public void setRedstoneState(boolean state) {
        this.redstoneState = state;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void invalidate() {
        super.invalidate();

        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
//                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
                addedToEnergyNet = false;
            }
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
//                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
                addedToEnergyNet = false;
            }
        }
    }

    public void onBlockBroken() {
        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
//                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
                addedToEnergyNet = false;
            }
        }
    }

    private int getRange() {
        int maxRange = RemoteIO.instance.defaultRange;
        maxRange += (upgradeCount(Upgrade.RANGE_T1) * RemoteIO.instance.rangeUpgradeT1Boost);
        maxRange += (upgradeCount(Upgrade.RANGE_T2) * RemoteIO.instance.rangeUpgradeT2Boost);
        maxRange += (upgradeCount(Upgrade.RANGE_T3) * RemoteIO.instance.rangeUpgradeT3Boost);
        maxRange += (upgradeCount(Upgrade.RANGE_WITHER) * RemoteIO.instance.rangeUpgradeWitherBoost);
        return maxRange;
    }
    
    private int upgradeCount(Upgrade upgrade) {
        return upgrade.enabled ? InventoryHelper.amountContained(upgrades, upgrade.toItemStack(), false) : 0;
    }

    public boolean hasUpgrade(Upgrade upgrade) {
        return InventoryHelper.inventoryContains(upgrades, upgrade.toItemStack(), false) && upgrade.enabled;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("turtleID", turtleID);
        
        nbt.setBoolean("unlimitedRange", unlimitedRange);
        nbt.setBoolean("remoteRequired", remoteRequired);
        nbt.setBoolean("state", lastClientState);
        
        if (upgrades != null) {
            NBTTagCompound upgradeNBT = new NBTTagCompound();
            InventoryHelper.writeToNBT(upgrades, upgradeNBT);
            nbt.setCompoundTag("upgrades", upgradeNBT);
        }
        
        if (camo != null) {
            NBTTagCompound camoNBT = new NBTTagCompound();
            InventoryHelper.writeToNBT(camo, camoNBT);
            nbt.setCompoundTag("camo", camoNBT);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        turtleID = nbt.getInteger("turtleID");
        
        unlimitedRange = nbt.getBoolean("unlimitedRange");
        remoteRequired = nbt.getBoolean("remoteRequired");
        lastClientState = nbt.getBoolean("state");
        
        if (nbt.hasKey("upgrades")) {
            ItemStack[] items = InventoryHelper.readFromNBT(upgrades, nbt.getCompoundTag("upgrades"));
            for (int i=0; i<items.length; i++) {
                this.upgrades.setInventorySlotContents(i, items[i]);
            }
        }
        
        if (nbt.hasKey("camo")) {
            ItemStack[] items = InventoryHelper.readFromNBT(camo, nbt.getCompoundTag("camo"));
            for (int i=0; i<items.length; i++) {
                this.camo.setInventorySlotContents(i, items[i]);
            }
        }
    }

    private IInventory getTurtleInventory() {
        if (this.turtleID < 0) {
            return null;
        }
        
        ITurtleAccess access = TurtleTracker.INSTANCE.getTurtleForID(this.turtleID);
        
        if (access == null) {
            return null;
        }
        
        if ((access.getWorld().provider.dimensionId != this.worldObj.provider.dimensionId) && !hasUpgrade(Upgrade.CROSS_DIMENSIONAL)) {
            return null;
        }
        
        Vec3 tileCoords = Vec3.fakePool.getVecFromPool(xCoord, yCoord, zCoord);
        
        if (tileCoords.distanceTo(access.getPosition()) > this.getRange()) {
            return null;
        }
        
        return new TurtleInventoryWrapper(access);
    }
    
    private IInventory getAndUpdate() {
        IInventory inv = getTurtleInventory();
        
        if (this.lastClientState != (inv != null)) {
            lastClientState = (inv != null);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, BlockHandler.blockBridgeID);
        }
        
        return inv;
    }
    
    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        return getAndUpdate() != null ? getAndUpdate().getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return getAndUpdate() != null ? getAndUpdate().getStackInSlot(i) : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return getAndUpdate() != null ? getAndUpdate().decrStackSize(i, j) : null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return getAndUpdate() != null ? getAndUpdate().getStackInSlotOnClosing(i) : null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (getAndUpdate() != null) getAndUpdate().setInventorySlotContents(i, itemstack);
    }

    @Override
    public String getInvName() {
        return getAndUpdate() != null ? getAndUpdate().getInvName() : "";
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
