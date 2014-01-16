package com.dmillerw.remoteIO.block.tile;

import cofh.api.energy.IEnergyHandler;
import com.dmillerw.remoteIO.core.helper.EnergyHelper;
import com.dmillerw.remoteIO.item.ItemTransmitter;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.DimensionalCoords;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TileRemoteInventory extends TileIOCore implements IInventory, IEnergySink, IEnergyHandler {

    public boolean addedToEnergyNet = false;
	public boolean remoteRequired = false;

	public String owner;

    @Override
    public void cleanup() {
        if (addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)this));
            addedToEnergyNet = false;
        }
    }

    @Override
    public DimensionalCoords connectionPosition() {
        MinecraftServer server = MinecraftServer.getServer();
        EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(owner);

        if (player != null) {
            return DimensionalCoords.create(player);
        } else {
            return null;
        }
    }

    @Override
    public Object getLinkedObject() {
        return getInventory();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (!addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile)this));
                addedToEnergyNet = true;
            }
        }
    }

	private InventoryPlayer getInventory() {
		MinecraftServer server = MinecraftServer.getServer();
		
		if (owner != null && !(owner.isEmpty()) && server != null && !redstoneState) {
			EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(owner);
			if (player != null) {
				if (inRange()) {
                    return (ItemTransmitter.hasSelfRemote(player) || remoteRequired) ? player.inventory : null;
				}
			}
		}
		
		return null;
	}

    private InventoryPlayer getInventory(Upgrade upgrade) {
        return hasUpgrade(upgrade) ? getInventoryAndUpdate() : null;
    }

	private InventoryPlayer getInventoryAndUpdate() {
        update();
        return getInventory();
    }

    @Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		super.writeCustomNBT(nbt);

        if (owner != null && !(owner.isEmpty())) {
			nbt.setString("owner", owner);
		}
		
		nbt.setBoolean("remoteRequired", remoteRequired);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);

		if (nbt.hasKey("owner")) {
			owner = nbt.getString("owner");
		} else {
			owner = "";
		}
		
		remoteRequired = nbt.getBoolean("remoteRequired");
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
