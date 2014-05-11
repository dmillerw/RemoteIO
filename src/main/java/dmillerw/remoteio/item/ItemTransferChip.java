package dmillerw.remoteio.item;

import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.*;

/**
 * @author dmillerw
 */
public class ItemTransferChip extends ItemSelectiveMeta {

	private IIcon[] icons;

	public ItemTransferChip() {
		super(new int[] {
			TransferType.MATTER_ITEM,
			TransferType.MATTER_FLUID,

			TransferType.ENERGY_IC2,
			TransferType.ENERGY_BC,
		},

		new String[] {
			"item",
			"fluid",

			"energy_ic2",
			"energy_mj"
		});

		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			//TODO Add support for eventual RemoteInventory
			if (world.getBlock(x, y, z) == HandlerBlock.remoteInterface) {
				TileRemoteInterface tile = (TileRemoteInterface) world.getTileEntity(x, y, z);
				ItemStack chip = stack.copy();
				chip.stackSize = 1;

				if (tile != null) {
					if (TileEntityHopper.func_145889_a(tile.transferChips, chip, ForgeDirection.UNKNOWN.ordinal()) == null) {
						if (stack.stackSize == 1) {
							player.setCurrentItemOrArmor(0, null);
						} else {
							ItemStack stack1 = stack.copy();
							stack1.stackSize = stack.stackSize - 1;
							player.setCurrentItemOrArmor(0, stack1);
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (pass == 1) {
			Color color = new Color(names.get(stack.getItemDamage()).hashCode()).brighter();
			return color.getRGB();
		}
		return 0xFFFFFF;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 1 ? icons[1] : icons[0];
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[2];
		icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
		icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "plate_transfer");
	}

}
