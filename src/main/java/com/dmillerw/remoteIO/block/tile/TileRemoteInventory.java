package com.dmillerw.remoteIO.block.tile;

import cofh.api.energy.IEnergyHandler;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.helper.EnergyHelper;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemTransmitter;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileRemoteInventory extends TileCore implements IInventory, IEnergySink, IEnergyHandler {

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
	
	public String owner;

    @Override
    public void updateEntity() {
        if (!addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            addedToEnergyNet = true;
        }
    }

    public void setRedstoneState(boolean state) {
        this.redstoneState = state;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
	private InventoryPlayer getInventory() {
		MinecraftServer server = MinecraftServer.getServer();
		
		if (owner != null && !(owner.isEmpty()) && server != null && !redstoneState) {
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(owner);
			if (player != null) {
				if ((player.worldObj.provider.dimensionId == this.worldObj.provider.dimensionId)) {
					if (Math.abs(player.getDistance(xCoord, yCoord, zCoord)) <= getRange() || unlimitedRange) {
						return (ItemTransmitter.hasSelfRemote(player) || remoteRequired) ? player.inventory : null;
					}
				} else {
					if (InventoryHelper.inventoryContains(upgrades, Upgrade.CROSS_DIMENSIONAL.toItemStack(), false)) {
						return (ItemTransmitter.hasSelfRemote(player) || remoteRequired) ? player.inventory : null;
					}
				}
			}
		}
		
		return null;
	}

    @Override
    public void invalidate() {
        super.invalidate();

        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
                addedToEnergyNet = false;
            }
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
                addedToEnergyNet = false;
            }
        }
    }

    public void onBlockBroken() {
        if (!worldObj.isRemote) {
            if (addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
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

    private InventoryPlayer getInventory(Upgrade upgrade) {
        return hasUpgrade(upgrade) ? getInventoryAndUpdate() : null;
    }

	private InventoryPlayer getInventoryAndUpdate() {
		InventoryPlayer inv = getInventory();
		boolean connection = inv != null;
		if (lastClientState != connection) {
			lastClientState = connection;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		return inv;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		if (owner != null && !(owner.isEmpty())) {
			nbt.setString("owner", owner);
		}
		
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
		if (nbt.hasKey("owner")) {
			owner = nbt.getString("owner");
		} else {
			owner = "";
		}
		
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

	@Override
	public int getSizeInventory() {
		return getInventory(Upgrade.ITEM) != null ? getInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInventory(Upgrade.ITEM) != null ? getInventory().getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInventory(Upgrade.ITEM) != null ? getInventory().decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getInventory(Upgrade.ITEM) != null ? getInventory().getStackInSlotOnClosing(i) : null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (getInventory(Upgrade.ITEM) != null) getInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return "";
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
		return getInventory(Upgrade.ITEM) != null ? getInventory().isItemValidForSlot(i, itemstack) : false;
	}

    /* IENERGYSINK */
    @Override
    public double demandedEnergyUnits() {
        IInventory inventory = getInventory(Upgrade.POWER_EU);
        return inventory != null ? EnergyHelper.requiresCharge(inventory, EnergyHelper.EnergyType.EU) ? 32D : 0D : 0D;
    }

    @Override
    public double injectEnergyUnits(ForgeDirection directionFrom, double amount) {
        IInventory inventory = getInventory(Upgrade.POWER_EU);
        return inventory != null ? EnergyHelper.distributeCharge(inventory, EnergyHelper.EnergyType.EU, (int) Math.floor(amount), false) : 0D;
    }

    @Override
    public int getMaxSafeInput() {
        return Integer.MAX_VALUE; // May change, but for now, no max
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        return getInventory(Upgrade.POWER_EU) != null;
    }

    /* IENERGYHANDLER */
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        IInventory inventory = getInventory(Upgrade.POWER_RF);
        return inventory != null ? EnergyHelper.distributeCharge(inventory, EnergyHelper.EnergyType.RF, maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canInterface(ForgeDirection from) {
        return getInventory(Upgrade.POWER_RF) != null;
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
