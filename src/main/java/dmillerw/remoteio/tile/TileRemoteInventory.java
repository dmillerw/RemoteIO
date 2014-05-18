package dmillerw.remoteio.tile;

import buildcraft.api.mj.MjAPI;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.core.helper.RotationHelper;
import dmillerw.remoteio.core.tracker.BlockTracker;
import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.InventoryNBT;
import dmillerw.remoteio.inventory.wrapper.InventoryArray;
import dmillerw.remoteio.item.HandlerItem;
import dmillerw.remoteio.item.ItemWirelessTransmitter;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.VisualState;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author dmillerw
 */
public class TileRemoteInventory extends TileIOCore implements InventoryNBT.IInventoryCallback, IInventory, IEnergyTile {

	@Override
	public void callback(IInventory inventory) {
		if (!hasWorldObj() || getWorldObj().isRemote) {
			return;
		}

		// I think IC2 caches tile state...
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		if (!registeredWithIC2 && hasTransferChip(TransferType.ENERGY_IC2) && getPlayer() != null) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			registeredWithIC2 = true;
		}

		// Clear missing upgrade flag
		missingUpgrade = false;

		updateVisualState();
		updateNeighbors();
	}

	public ItemStack simpleCamo;

	public VisualState visualState = VisualState.INACTIVE;

	public InventoryNBT transferChips = new InventoryNBT(this, 9, 1);
	public InventoryNBT upgradeChips = new InventoryNBT(this, 9, 1);

	public String target;

	private boolean registeredWithIC2 = false;
	private boolean missingUpgrade = false;

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		transferChips.writeToNBT("TransferItems", nbt);
		upgradeChips.writeToNBT("UpgradeItems", nbt);

		if (target != null && !target.isEmpty()) {
			nbt.setString("target", target);
		}

		// This is purely to ensure the client remains synchronized upon world load
		if (simpleCamo != null) {
			NBTTagCompound tag = new NBTTagCompound();
			simpleCamo.writeToNBT(tag);
			nbt.setTag("simple", tag);
		}
		nbt.setByte("state", (byte) visualState.ordinal());
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		transferChips.readFromNBT("TransferItems", nbt);
		upgradeChips.readFromNBT("UpgradeItems", nbt);

		if (nbt.hasKey("target")) {
			target = nbt.getString("target");
		} else {
			target = "";
		}

		// This is purely to ensure the client remains synchronized upon world load
		if (nbt.hasKey("simple")) {
			simpleCamo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("simple"));
		}
		visualState = VisualState.values()[nbt.getByte("state")];
	}

	@Override
	public void onClientUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("state")) {
			visualState = VisualState.values()[nbt.getByte("state")];
		}

		if (nbt.hasKey("simple")) {
			simpleCamo = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("simple"));
		} else if (nbt.hasKey("simple_null")) {
			simpleCamo = null;
		}
	}

	@Override
	public void onChunkUnload() {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}
	}

	@Override
	public void invalidate() {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}
	}

	/* CHIP METHODS */

	public EntityPlayer getPlayer() {
		if (target == null || target.isEmpty()) {
			return null;
		}

		ServerConfigurationManager configurationManager = MinecraftServer.getServer().getConfigurationManager();
		EntityPlayer player = configurationManager.getPlayerForUsername(target);

		if (player != null) {
			if (!ItemWirelessTransmitter.hasValidRemote(player)) {
				return null;
			}
		}

		return player;
	}
	public InventoryArray getPlayerInventory() {
		EntityPlayer player = getPlayer();
		if (player != null) return new InventoryArray(player.inventory.mainInventory);
		return null;
	}

	public boolean hasTransferChip(int type) {
		return InventoryHelper.containsStack(transferChips, new ItemStack(HandlerItem.transferChip, 1, type), true, false);
	}

	public boolean hasUpgradeChip(int type) {
		return InventoryHelper.containsStack(upgradeChips, new ItemStack(HandlerItem.upgradeChip, 1, type), true, false);
	}

	/* END CHIP METHODS */

	public void setPlayer(EntityPlayer player) {
		if (registeredWithIC2) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			registeredWithIC2 = false;
		}

		target = player.getCommandSenderName();

		if (!registeredWithIC2 && hasTransferChip(TransferType.ENERGY_IC2)) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			registeredWithIC2 = true;
		}

		updateVisualState();
		updateNeighbors();
		markForUpdate();
	}

	public void updateSimpleCamo() {
		ItemStack stack = null;
		for (ItemStack stack1 : InventoryHelper.toArray(upgradeChips)) {
			if (stack1 != null && stack1.getItemDamage() == UpgradeType.SIMPLE_CAMO) {
				stack = new InventoryItem(stack1, 1).getStackInSlot(0);
				if (stack != null) {
					break;
				}
			}
		}

		simpleCamo = stack;

		NBTTagCompound nbt = new NBTTagCompound();
		if (stack != null) {
			NBTTagCompound tag = new NBTTagCompound();
			stack.writeToNBT(tag);
			nbt.setTag("simple", tag);
		} else {
			nbt.setBoolean("simple_null", true);
		}
		sendClientUpdate(nbt);
	}

	public void updateVisualState() {
		setVisualState(calculateVisualState());
	}

	private VisualState calculateVisualState() {
		if (target == null || target.isEmpty()) {
			return VisualState.INACTIVE;
		} else {
			EntityPlayer player = getPlayer();

			if (player == null) {
				return VisualState.INACTIVE_BLINK;
			}

			boolean simple = hasUpgradeChip(UpgradeType.SIMPLE_CAMO);
			boolean remote = hasUpgradeChip(UpgradeType.REMOTE_CAMO);

			if (simple && !remote) {
				return VisualState.CAMOUFLAGE_SIMPLE;
			} else if (!simple && remote) {
				return VisualState.CAMOUFLAGE_REMOTE;
			} else if (simple && remote) {
				return VisualState.CAMOUFLAGE_BOTH;
			}

			return missingUpgrade ? VisualState.ACTIVE_BLINK : VisualState.ACTIVE;
		}
	}

	/** Sends the passed in visual state to the client, calling other update methods if applicable */
	private void setVisualState(VisualState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		sendClientUpdate(nbt);

		if (state == VisualState.CAMOUFLAGE_SIMPLE || state == VisualState.CAMOUFLAGE_BOTH) {
			updateSimpleCamo();
		}

		visualState = state;
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.getStackInSlot(slot) : null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.decrStackSize(slot, amount) : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.getStackInSlotOnClosing(slot) : null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		InventoryArray inventoryPlayer = getPlayerInventory();
		if (inventoryPlayer != null) inventoryPlayer.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.getInventoryStackLimit() : 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		InventoryArray inventoryPlayer = getPlayerInventory();
		return inventoryPlayer != null ? inventoryPlayer.isItemValidForSlot(slot, stack) : false;
	}

}
