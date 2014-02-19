package com.dmillerw.remoteIO.block.tile;

import cofh.api.energy.IEnergyContainerItem;
import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.lib.DimensionalCoords;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Created by Dylan Miller on 1/13/14
 */
public abstract class TileIOCore extends TileCore {

    /* NORMAL FUEL */
    public static ItemStack fuelStack = new ItemStack(Item.enderPearl);

    public static boolean consumeOnlyWhenActive = true;
    public static boolean requireFuel = false;

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
    protected boolean unlimitedRange = false;
    protected boolean requiresUpgrades = true;
    public boolean requiresPower = true;

    /* COMMON FLAGS */
    private boolean firstLoad = true;
    public boolean redstoneState = false;

    /* CLIENT FLAGS */
    public boolean lastClientState;

    /* WARNINGS */
    public boolean warningsChanged = true;

    public EnumSet<Warning> activeWarnings = EnumSet.noneOf(Warning.class);

    public void resetWarnings() {
        activeWarnings.clear();
        warningsChanged = true;
    }

    public void addWarning(Warning warning) {
        activeWarnings.add(warning);
        warningsChanged = true;
    }

    public void calculateWarnings() {
        resetWarnings();

        if (connectionPosition() == null) {
            addWarning(Warning.NO_CONNECTION);
        } else {
            if (!connectionExists()) {
                addWarning(Warning.OUT_OF_RANGE);
            } else if (!objectExists()) {
                addWarning(Warning.NO_OBJECT);
            }
        }

        if (!hasFuel()) {
            addWarning(Warning.NO_FUEL);
        }
    }

    /** Should be run whenever an aspect of this block changes.
     *  By default just sends a visual update to the client */
    public void update() {
        if (worldObj.isRemote) {
            return;
        }

        boolean isConnected = objectExists() && !redstoneState;
        if (isConnected != lastClientState) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("state", isConnected);
            tag.setBoolean("redstone", redstoneState);
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

    /** Returns the actual object this block is connected to. Only used to verify connection status.
     *  Calling this should never initiate an update, but all interaction should stem from the object returned here */
    public abstract Object getLinkedObject();

    public boolean hasUpgrade(ItemUpgrade.Upgrade upgrade, boolean warn) {
        if (InventoryHelper.inventoryContains(upgrades, upgrade.toItemStack(), false) || !requiresUpgrades) {
            return true;
        } else {
            if (warn) {
                addWarning(Warning.MISSING_UPGRADE);
            }
            return false;
        }
    }

    protected boolean connectionExists() {
        if (connectionPosition() == null) {
            return false;
        }

        if (connectionPosition().inWorld(this.worldObj)) {
            DimensionalCoords coords = DimensionalCoords.create(this);
            return (requiresUpgrades && coords.withinRange(connectionPosition(), getMaxRange())) || unlimitedRange;
        } else {
            return (requiresUpgrades && !hasUpgrade(ItemUpgrade.Upgrade.CROSS_DIMENSIONAL, true)) || unlimitedRange;
        }
    }

    protected boolean objectExists() {
        return connectionExists() && getLinkedObject() != null;
    }

    protected boolean canConnect() {
        if (!objectExists()) {
            return false;
        }

        if (!hasFuel()) {
            return false;
        }

        if (redstoneState) {
            return false;
        }

        return true;
    }

    protected boolean hasFuel() {
        if ((this.requiresPower && fuelPerTick > 0 && requireFuel) && !fuelHandler.consumeFuel(fuelPerTick, true)) {
            return false;
        }

        return true;
    }

    public int getMaxRange() {
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
                onNeighborBlockUpdate();
                firstLoad = false;
            }

            /* Add fuel from fuel slot */
            if (worldObj.getTotalWorldTime() % 5 == 0) {
                if (this.requiresPower && fuelPerTick > 0 && requireFuel) {
                    ItemStack fuel = this.fuel.getStackInSlot(0);

                    if (fuel != null) {
                        if (fuel.isItemEqual(fuelStack)) {
                            if (this.fuelHandler.addFuel(fuelPerStack, false)) {
                                this.fuel.decrStackSize(0, 1);
                            }
                        } else if (fuel.getItem() instanceof IEnergyContainerItem) {
                            if (((IEnergyContainerItem)fuel.getItem()).extractEnergy(fuel, rfPerFuel, true) == rfPerFuel) {
                                if (this.fuelHandler.addFuel(1, false)) {
                                    ((IEnergyContainerItem)fuel.getItem()).extractEnergy(fuel, rfPerFuel, false);
                                }
                            }
                        } else if (fuel.getItem() instanceof IElectricItem) {
                            if (ElectricItem.manager.discharge(fuel, euPerFuel, ((IElectricItem)fuel.getItem()).getTier(fuel), false, true) == euPerFuel) {
                                if (this.fuelHandler.addFuel(1, false)) {
                                    ElectricItem.manager.discharge(fuel, euPerFuel, ((IElectricItem)fuel.getItem()).getTier(fuel), false, false);
                                }
                            }
                        }
                    }
                }
            }

            /* Consume fuel */
            if ((this.requiresPower && fuelPerTick > 0 && requireFuel) && (lastClientState || !consumeOnlyWhenActive) && worldObj.getTotalWorldTime() % 20 == 0) {
                this.fuelHandler.consumeFuel(fuelPerTick, false);
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
        redstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && hasUpgrade(ItemUpgrade.Upgrade.REDSTONE, true);
        update();
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

    public static enum Warning {
        NO_CONNECTION,
        NO_OBJECT,
        NO_FUEL,
        OUT_OF_RANGE,
        MISSING_UPGRADE;

        public String getDescription() {
            return I18n.getString("warning." + this.toString().toLowerCase() + ".desc");
        }

        public String getTooltip() {
            return I18n.getString("warning." + this.toString().toLowerCase() + ".tooltip");
        }
    }

}
