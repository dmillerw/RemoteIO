package dmillerw.remoteio.item;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.block.HandlerBlock;
import dmillerw.remoteio.block.tile.TileRemoteInterface;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;
import dmillerw.remoteio.core.handler.GuiHandler;
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
public class ItemUpgradeChip extends ItemSelectiveMeta {

	private IIcon[] icons;

	public ItemUpgradeChip() {
		super(new int[] {
			UpgradeType.REMOTE_CAMO,
			UpgradeType.REMOTE_ACCESS,
			UpgradeType.SIMPLE_CAMO
		},

		new String[] {
			"remote_camo",
			"remote_access",
			"simple_camo"
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
					if (TileEntityHopper.func_145889_a(tile.upgradeChips, chip, ForgeDirection.UNKNOWN.ordinal()) == null) {
						tile.callback(tile.upgradeChips);
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				switch(stack.getItemDamage()) {
					case UpgradeType.SIMPLE_CAMO: player.openGui(RemoteIO.instance, GuiHandler.GUI_SIMPLE_CAMO, world, 0, 0, 0); break;
				}
			}
		}

		return stack;
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
		icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "plate_upgrade");
	}

}
