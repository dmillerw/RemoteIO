package dmillerw.remoteio.item;

import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemWirelessTransmitter extends Item {

	private IIcon icon;

	public ItemWirelessTransmitter() {
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

			if (tile != null && tile instanceof TileRemoteInterface) {
				ItemLocationChip.setCoordinates(stack, DimensionalCoords.create(tile));
				player.addChatComponentMessage(new ChatComponentTranslation("chat.location_chip.save"));
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("position")) {
				DimensionalCoords coord = ItemLocationChip.getCoordinates(stack);
				coord.getBlock().onBlockActivated(world, coord.x, coord.y, coord.z, player, -1, 0, 0, 0);
			}
		}

		return stack;
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "transmitter");
	}
}
