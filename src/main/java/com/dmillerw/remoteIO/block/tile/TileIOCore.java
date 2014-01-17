package com.dmillerw.remoteIO.block.tile;

import cofh.api.energy.IEnergyContainerItem;
import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.lib.DimensionalCoords;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * Created by Dylan Miller on 1/13/14
 */
public abstract class TileIOCore extends TileCore {

    /* NORMAL FUEL */
    public static ItemStack fuelStack = new ItemStack(Item.enderPearl);

    public static int fuelPerStack = 3600;
    public static int fuelPerTick = 1;

    /* ENERGY */
    public static int rfPerFuel = 350;
    public static int euPerFuel = 5;

    public IInventory upgrades = new InventoryBasic("Upgrades", false, 9);
    public IInventory camo = new InventoryBasic("Camo", false, 1) {
        @Override
        public void onInventoryChanged() {
            super.onInventoryChanged();
            if (worldObj != null) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    };
    public IInventory fuel = new InventoryBasic("Fuel", false, 1);

    public FuelHandler fuelHandler = new FuelHandler(-1);

    /* NBT FLAGS */
    private boolean unlimitedRange = false;
    private boolean requiresUpgrades = true;
    public boolean requiresPower = true;

    /* COMMON FLAGS */
    private boolean firstLoad = true;
    public boolean redstoneState = false;

    /* CLIENT FLAGS */
    public boolean lastClientState;

    /** Should be run whenever an aspect of this block changes.
     *  By default just sends a visual update to the client */
    public void update() {
        boolean isConnected = getLinkedObject() != null;
        if (isConnected != lastClientState) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("state", isConnected);
            sendClientUpdate(tag);

            lastClientState = isConnected;
        }
    }

    /** Run whenever this block is unloaded for whatever reason */
    public abstract void cleanup();

    /** Returns the position of whatever the block is connected to. Used to simplify range handling */
    public abstract DimensionalCoords connectionPosition();

    public World getLinkedWorld() {
        if (connectionPosition() != null) {
            return MinecraftServer.getServer().worldServerForDimension(connectionPosition().dimensionID);
        } else {
            return null;
        }
    }

    /** Returns the actual object this block is connected to. Only used to verify connection status */
    public abstract Object getLinkedObject();

    public boolean hasUpgrade(ItemUpgrade.Upgrade upgrade) {
        return InventoryHelper.inventoryContains(upgrades, upgrade.toItemStack(), false) || !requiresUpgrades;
    }

    protected boolean inRange() {
        if (connectionPosition() == null) {
            return false;
        }

        if (requiresPower && !fuelHandler.consumeFuel(fuelPerTick)) {
            return false;
        }

        if (redstoneState) {
            return false;
        }

        if (connectionPosition().inWorld(this.worldObj)) {
            DimensionalCoords coords = DimensionalCoords.create(this);
            return (requiresUpgrades && coords.getRangeTo(connectionPosition()) <= getMaxRange()) || unlimitedRange;
        } else {
            return requiresUpgrades || hasUpgrade(ItemUpgrade.Upgrade.CROSS_DIMENSIONAL);
        }
    }

    private int getMaxRange() {
        int maxRange = RemoteIO.instance.defaultRange;
        ItemUpgrade.Upgrade[] rangeUpgrades = new ItemUpgrade.Upgrade[] {ItemUpgrade.Upgrade.RANGE_T1, ItemUpgrade.Upgrade.RANGE_T2, ItemUpgrade.Upgrade.RANGE_T3, ItemUpgrade.Upgrade.RANGE_WITHER};

        for (ItemUpgrade.Upgrade upgrade : rangeUpgrades) {
            for (int i=0; i < InventoryHelper.amountContained(upgrades, upgrade.toItemStack(), false); i++) {
                switch(upgrade) {
                    case RANGE_T1:     maxRange += RemoteIO.instance.rangeUpgradeT1Boost;     break;
                    case RANGE_T2:     maxRange += RemoteIO.instance.rangeUpgradeT2Boost;     break;
                    case RANGE_T3:     maxRange += RemoteIO.instance.rangeUpgradeT3Boost;     break;
                    case RANGE_WITHER: maxRange += RemoteIO.instance.rangeUpgradeWitherBoost; break;
                    default: break;
                }
            }
        }
        return maxRange;
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (firstLoad) {
                update();
                onNeighborBlockUpdate();
                firstLoad = false;
            }

            /* Add fuel from fuel slot */
            if (worldObj.getTotalWorldTime() % 5 == 0) {
                if (requiresPower) {
                    ItemStack fuel = this.fuel.getStackInSlot(0);

                    if (fuel != null) {
                        if (fuel.isItemEqual(fuelStack)) {
                            if (this.fuelHandler.addFuel(fuelPerStack)) {
                                this.fuel.decrStackSize(0, 1);
                            }
                        } else if (fuel.getItem() instanceof IEnergyContainerItem) {
                            if (((IEnergyContainerItem)fuel.getItem()).extractEnergy(fuel, rfPerFuel, true) == rfPerFuel) {
                                if (this.fuelHandler.addFuel(1)) {
                                    ((IEnergyContainerItem)fuel.getItem()).extractEnergy(fuel, rfPerFuel, false);
                                }
                            }
                        } else if (fuel.getItem() instanceof IElectricItem) {
                            if (ElectricItem.manager.discharge(fuel, euPerFuel, ((IElectricItem)fuel.getItem()).getTier(fuel), false, true) == euPerFuel) {
                                if (this.fuelHandler.addFuel(1)) {
                                    ElectricItem.manager.discharge(fuel, euPerFuel, ((IElectricItem)fuel.getItem()).getTier(fuel), false, false);
                                }
                            }
                        }
                    }
                }
            }

            /* Consume fuel */
            if (requiresPower && worldObj.getTotalWorldTime() % 20 == 0) {
                this.fuelHandler.consumeFuel(fuelPerTick);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (!worldObj.isRemote) {
            cleanup();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        if (!worldObj.isRemote) {
            cleanup();
        }
    }

    @Override
    public void onBlockBreak() {
        if (!worldObj.isRemote) {
            cleanup();
        }
    }

    @Override
    public void onNeighborBlockUpdate() {
        redstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("redstone", redstoneState);
        sendClientUpdate(tag);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("unlimitedRange", unlimitedRange);
        nbt.setBoolean("requiresUpgrades", requiresUpgrades);
        nbt.setBoolean("requiresPower", requiresPower);

        NBTTagCompound fuelHandlerNBT = new NBTTagCompound();
        this.fuelHandler.writeToNBT(fuelHandlerNBT);
        nbt.setCompoundTag("fuelHandler", fuelHandlerNBT);

        NBTTagCompound upgradeNBT = new NBTTagCompound();
        InventoryHelper.writeToNBT(upgrades, upgradeNBT);
        nbt.setCompoundTag("upgrades", upgradeNBT);

        NBTTagCompound camoNBT = new NBTTagCompound();
        InventoryHelper.writeToNBT(camo, camoNBT);
        nbt.setCompoundTag("camo", camoNBT);

        NBTTagCompound fuelNBT = new NBTTagCompound();
        InventoryHelper.writeToNBT(fuel, fuelNBT);
        nbt.setCompoundTag("fuel", fuelNBT);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        unlimitedRange = nbt.getBoolean("unlimitedRange");
        requiresUpgrades = nbt.getBoolean("requiresUpgrades");
        requiresPower = nbt.getBoolean("requiresPower");

        this.fuelHandler = FuelHandler.fromNBT(nbt.getCompoundTag("fuelHandler"));

        ItemStack[] upgradesArray = InventoryHelper.readFromNBT(upgrades, nbt.getCompoundTag("upgrades"));
        for (int i=0; i<upgradesArray.length; i++) {
            this.upgrades.setInventorySlotContents(i, upgradesArray[i]);
        }

        ItemStack[] camoArray = InventoryHelper.readFromNBT(camo, nbt.getCompoundTag("camo"));
        for (int i=0; i<camoArray.length; i++) {
            this.camo.setInventorySlotContents(i, camoArray[i]);
        }

        ItemStack[] fuelArray = InventoryHelper.readFromNBT(fuel, nbt.getCompoundTag("fuel"));
        for (int i=0; i<fuelArray.length; i++) {
            this.fuel.setInventorySlotContents(i, fuelArray[i]);
        }
    }

    @Override
    public void onClientUpdate(NBTTagCompound nbt) {
        if (nbt.hasKey("state")) {
            lastClientState = nbt.getBoolean("state");
        }

        if (nbt.hasKey("redstone")) {
            redstoneState = nbt.getBoolean("redstone");
        }
    }

}
