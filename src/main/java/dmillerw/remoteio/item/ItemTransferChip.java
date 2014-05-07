package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * @author dmillerw
 */
public class ItemTransferChip extends ItemSelectiveMeta {

	private IIcon[] icons;

	public ItemTransferChip() {
		super(new int[] {
			TransferType.MATTER_ITEM,
			TransferType.MATTER_FLUID
		},

		new String[] {
			"item",
			"fluid"
		});

		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 1 ? names.get(stack.getItemDamage()).hashCode() : 0xFFFFFF;
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
		icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "upgrade");
		icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "upgrade_overlay");
	}

}
