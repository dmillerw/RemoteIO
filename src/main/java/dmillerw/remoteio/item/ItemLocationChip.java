package dmillerw.remoteio.item;

import dmillerw.remoteio.block.BlockRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemLocationChip extends Item {

	public static void setCoordinates(ItemStack stack, DimensionalCoords coords) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = stack.getTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		coords.writeToNBT(tag);
		nbt.setTag("position", tag);
		stack.setTagCompound(nbt);
	}

	public static DimensionalCoords getCoordinates(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}

		NBTTagCompound nbt = stack.getTagCompound();

		if (!nbt.hasKey("position")) {
			return null;
		}

		return DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
	}

	private IIcon icon;

	public ItemLocationChip() {
		super();

		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
		DimensionalCoords coords = ItemLocationChip.getCoordinates(stack);

		if (coords != null) {
			list.add("Dimension: " + DimensionManager.getProvider(coords.dimensionID).getDimensionName());
			list.add("X: " + coords.x + " Y: " + coords.y + " Z: " + coords.z);
		}
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(x, y, z);

			if (player.isSneaking()) {
				ItemLocationChip.setCoordinates(stack, new DimensionalCoords(world.provider.dimensionId, x, y, z));
				player.addChatComponentMessage(new ChatComponentTranslation("chat.target.save"));
			} else {
				if (tile != null) {
					if (tile instanceof TileRemoteInterface) {
						DimensionalCoords coords = ItemLocationChip.getCoordinates(stack);

						if (coords != null) {
							if (coords.getBlock() instanceof BlockRemoteInterface) {
                                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.loop"));
                            } else {
                                ((TileRemoteInterface)tile).setRemotePosition(coords);
                                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.load"));
                            }
						}
					}
				}
			}
		}

		return !world.isRemote;
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return false;
	}

}
