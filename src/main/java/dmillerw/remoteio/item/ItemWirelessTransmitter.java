package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

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
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "transmitter");
	}
}
