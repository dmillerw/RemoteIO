package dmillerw.remoteio.block.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dmillerw.remoteio.lib.DimensionalCoords;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public class TileRemoteInterface extends TileIOCore {

	@SideOnly(Side.CLIENT)
	public VisualState visualState = VisualState.INACTIVE;

	public DimensionalCoords remotePosition;

	private boolean visualDirty = true;

	@Override
	public void writeCustomNBT(NBTTagCompound nbt) {
		if (remotePosition != null) {
			NBTTagCompound tag = new NBTTagCompound();
			remotePosition.writeToNBT(tag);
			nbt.setTag("position", tag);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("position")) {
			remotePosition = DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
		}
	}

	@Override
	public void onClientUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("state")) {
			visualState = VisualState.values()[nbt.getByte("state")];
		}
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (visualDirty) {
				setVisualState(calculateVisualState());
			}
		}
	}

	public VisualState calculateVisualState() {
		if (remotePosition == null) {
			return VisualState.INACTIVE;
		} else {
			// More here eventually
			return VisualState.ACTIVE;
		}
	}

	public void setVisualState(VisualState state) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("state", (byte)state.ordinal());
		sendClientUpdate(nbt);
	}

	public static enum VisualState {
		INACTIVE,
		INACTIVE_BLINK,
		ACTIVE,
		ACTIVE_BLINK,
		REMOTE_CAMO;
	}

}
