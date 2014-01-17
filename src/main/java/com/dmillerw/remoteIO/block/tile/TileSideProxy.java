package com.dmillerw.remoteIO.block.tile;

import appeng.api.DimentionalCoord;
import appeng.api.WorldCoord;
import appeng.api.events.GridTileLoadEvent;
import appeng.api.events.GridTileUnloadEvent;
import appeng.api.me.tiles.IGridTeleport;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.BlockState;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.ITrackerCallback;
import com.dmillerw.remoteIO.core.tracker.BlockTracker.TrackedBlock;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.DimensionalCoords;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileSideProxy extends TileIOCore implements ITrackerCallback, IInventory, ISidedInventory, IFluidHandler, IPowerReceptor, IPowerEmitter, IEnergyHandler, IEnergyStorage, IEnergySource, IEnergySink, IGridTileEntity, IGridTeleport{

    private IGridInterface aeGrid = null;

    private boolean addedToMENetwork = false;
    private boolean addedToEnergyNet = false;

    public DimensionalCoords coords;

    public ForgeDirection insertionSide = ForgeDirection.UNKNOWN;

    @Override
    public void onBlockChanged(TrackedBlock tracked) {
        if (connectionPosition() != null) {
            if (tracked.state == BlockState.REMOVED) {
                update();
            }
        } else {
            tracked.destroy();
        }
    }

    @Override
    public int getTrackerHash() {
        return this.coords != null ? this.coords.hashCode() : 0;
    }

    @Override
    public void cleanup() {
        if (addedToMENetwork) {
            MinecraftForge.EVENT_BUS.post(new GridTileUnloadEvent(this, this.worldObj, this.getLocation()));
            addedToMENetwork = false;
        }

        if (addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
            addedToEnergyNet = false;
        }

        if (coords != null) {
            BlockTracker.getInstance().stopTracking(coords.getWorld().provider.dimensionId, coords.x, coords.y, coords.z);
        }
    }

    @Override
    public DimensionalCoords connectionPosition() {
        return insertionSide != ForgeDirection.UNKNOWN ? this.coords : null;
    }

    @Override
    public Object getLinkedObject() {
        if (this.worldObj.isRemote) {
            return null;
        }

        if (!connectionExists()) {
            return null;
        }

        World world = getLinkedWorld();
        return world != null ? world.getBlockTileEntity(coords.x, coords.y, coords.z) : null;
    }

    @Override
    public int getMaxRange() {
        return 1;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (!addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile)this));
                addedToEnergyNet = true;
            }

            if (!addedToMENetwork) {
                MinecraftForge.EVENT_BUS.post(new GridTileLoadEvent(this, this.worldObj, this.getLocation()));
                addedToMENetwork = true;
            }
        }
    }

    public void setCoordinates(int x, int y, int z, int d) {
        this.coords = new DimensionalCoords(d, x, y, z);
        update();
    }

    public void clearCoordinates() {
        this.coords = null;
        update();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);

        if (this.coords != null) {
            NBTTagCompound coordsNBT = new NBTTagCompound();
            coords.writeToNBT(coordsNBT);
            nbt.setCompoundTag("coords", coordsNBT);
        }

        nbt.setByte("side", (byte) insertionSide.ordinal());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);

        if (nbt.hasKey("coords")) {
            NBTTagCompound coordsNBT = nbt.getCompoundTag("coords");
            this.coords = DimensionalCoords.fromNBT(coordsNBT);
        }

        insertionSide = ForgeDirection.values()[nbt.getByte("side")];
    }

    public TileEntity getTileEntity() {
        Object obj = getLinkedObject();

        if (!canConnect()) {
            return null;
        }

        return (TileEntity) obj;
    }

    public TileEntity getTileWithUpdate() {
        update();
        return getTileEntity();
    }

    /* INTERACTION HANDLING */
    private IInventory getInventory() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IInventory && hasUpgrade(Upgrade.ITEM, false)) {
            return (IInventory)getTileWithUpdate();
        }

        return null;
    }

    private ISidedInventory getSidedInventory() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof ISidedInventory && hasUpgrade(Upgrade.ISIDED_AWARE, false)) {
            return (ISidedInventory)getTileWithUpdate();
        }

        return null;
    }

    private IFluidHandler getFluidHandler() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IFluidHandler && hasUpgrade(Upgrade.FLUID, false)) {
            return (IFluidHandler)getTileWithUpdate();
        }

        return null;
    }

    private IPowerReceptor getBCPowerReceptor() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IPowerReceptor && hasUpgrade(Upgrade.POWER_MJ, false)) {
            return (IPowerReceptor)getTileWithUpdate();
        }

        return null;
    }

    private IPowerEmitter getBCPowerEmitter() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IPowerEmitter && hasUpgrade(Upgrade.POWER_MJ, false)) {
            return (IPowerEmitter)getTileWithUpdate();
        }

        return null;
    }

    private IEnergyHandler getRFHandler() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IEnergyHandler && hasUpgrade(Upgrade.POWER_RF, false)) {
            return (IEnergyHandler)getTileWithUpdate();
        }

        return null;
    }

    private IEnergyStorage getRFSource() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IEnergyStorage && hasUpgrade(Upgrade.POWER_RF, false)) {
            return (IEnergyStorage)getTileWithUpdate();
        }

        return null;
    }

    private IEnergySource getEUSource() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IEnergySource && hasUpgrade(Upgrade.POWER_EU, false)) {
            return (IEnergySource)getTileWithUpdate();
        }

        return null;
    }

    private IEnergySink getEUSink() {
        if (getTileWithUpdate() != null && getTileWithUpdate() instanceof IEnergySink && hasUpgrade(Upgrade.POWER_EU, false)) {
            return (IEnergySink)getTileWithUpdate();
        }

        return null;
    }

    /* IINVENTORY */
    @Override
    public int getSizeInventory() {
        return getInventory() != null ? getInventory().getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return getInventory() != null ? getInventory().getStackInSlot(i) : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return getInventory() != null ? getInventory().decrStackSize(i, j) : null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return getInventory() != null ? getInventory().getStackInSlotOnClosing(i) : null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        getInventory().setInventorySlotContents(i, itemstack);
    }

    @Override
    public String getInvName() {
        return getInventory() != null ? getInventory().getInvName() : null;
    }

    @Override
    public boolean isInvNameLocalized() {
        return getInventory() != null ? getInventory().isInvNameLocalized() : false;
    }

    @Override
    public int getInventoryStackLimit() {
        return getInventory() != null ? getInventory().getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openChest() {

    }

    @Override
    public void closeChest() {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return getInventory() != null ? getInventory().isItemValidForSlot(i, itemstack) : false;
    }

    /* ISIDEDINVENTORY */
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return getSidedInventory() != null ? getSidedInventory().getAccessibleSlotsFromSide(insertionSide.ordinal()) : new int[0];
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        return getSidedInventory() != null ? getSidedInventory().canInsertItem(insertionSide.ordinal(), itemstack, j) : true;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        return getSidedInventory() != null ? getSidedInventory().canExtractItem(insertionSide.ordinal(), itemstack, j) : true;
    }

    /* IFLUIDHANDLER */
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return getFluidHandler() != null ? getFluidHandler().fill(insertionSide, resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return getFluidHandler() != null ? getFluidHandler().drain(insertionSide, resource, doDrain) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return getFluidHandler() != null ? getFluidHandler().drain(insertionSide, maxDrain, doDrain) : null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return getFluidHandler() != null ? getFluidHandler().canFill(insertionSide, fluid) : false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return getFluidHandler() != null ? getFluidHandler().canDrain(insertionSide, fluid) : false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return getFluidHandler() != null ? getFluidHandler().getTankInfo(insertionSide) : new FluidTankInfo[0];
    }

    /* IPOWEREMITTER */
    @Override
    public boolean canEmitPowerFrom(ForgeDirection side) {
        return getBCPowerEmitter() != null ? getBCPowerEmitter().canEmitPowerFrom(insertionSide) : false;
    }

    /* IPOWERRECEPTOR */
    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection side) {
        return getBCPowerReceptor() != null ? getBCPowerReceptor().getPowerReceiver(insertionSide) : null;
    }

    @Override
    public void doWork(PowerHandler workProvider) {
        if (getBCPowerReceptor() != null) getBCPowerReceptor().doWork(workProvider);
    }

    /* IGRIDTILEENTITY */
    @Override
    public WorldCoord getLocation() {
        return new WorldCoord(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean isValid() {
        return coords != null && hasUpgrade(Upgrade.AE, false) && canConnect();
    }

    @Override
    public void setPowerStatus(boolean hasPower) {}

    @Override
    public boolean isPowered() {
        return coords != null && hasUpgrade(Upgrade.AE, false) && canConnect();
    }

    @Override
    public IGridInterface getGrid() {
        return aeGrid;
    }

    @Override
    public void setGrid(IGridInterface gi) {
        aeGrid = gi;
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    /* IENERGYHANDLER */
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return getRFHandler() != null ? getRFHandler().receiveEnergy(insertionSide, maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return getRFHandler() != null ? getRFHandler().extractEnergy(insertionSide, maxExtract, simulate) : 0;
    }

    @Override
    public boolean canInterface(ForgeDirection from) {
        return getRFHandler() != null ? getRFHandler().canInterface(insertionSide) : false;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getRFHandler() != null ? getRFHandler().getEnergyStored(insertionSide) : 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getRFHandler() != null ? getRFHandler().getMaxEnergyStored(insertionSide) : 0;
    }

    /* IENERGYSTORAGE */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return getRFSource() != null ? getRFSource().receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return getRFSource() != null ? getRFSource().extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return getRFSource() != null ? getRFSource().getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getRFSource() != null ? getRFSource().getMaxEnergyStored() : 0;
    }

    /* IENERGYSOURCE */
    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
        return getEUSource() != null ? getEUSource().emitsEnergyTo(receiver, insertionSide) : false;
    }

    @Override
    public double getOfferedEnergy() {
        return getEUSource() != null ? getEUSource().getOfferedEnergy() : 0;
    }

    @Override
    public void drawEnergy(double amount) {
        if (getEUSource() != null) getEUSource().drawEnergy(amount);
    }

    /* IENERGYSINK */
    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return getEUSink() != null ? getEUSink().acceptsEnergyFrom(emitter, insertionSide) : false;
    }

    @Override
    public double demandedEnergyUnits() {
        return getEUSink() != null ? getEUSink().demandedEnergyUnits() : 0;
    }

    @Override
    public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
        return getEUSink() != null ? getEUSink().injectEnergyUnits(insertionSide, amount) : 0;
    }

    @Override
    public int getMaxSafeInput() {
        return getEUSink() != null ? getEUSink().getMaxSafeInput() : Integer.MAX_VALUE;
    }

    /* IGRIDTELEPORT */
    @Override
    public DimentionalCoord[] findRemoteSide() {
        return getLinkedWorld() != null ? new DimentionalCoord[] {new DimentionalCoord(getLinkedWorld(), (int)coords.x, (int)coords.y, (int)coords.z)} : new DimentionalCoord[0];
    }

}
