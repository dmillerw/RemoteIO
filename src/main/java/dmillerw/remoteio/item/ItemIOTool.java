package dmillerw.remoteio.item;

import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

/**
 * @author dmillerw
 */
public class ItemIOTool extends Item implements IIOTool {

	private IIcon icon;

	public ItemIOTool() {
		super();

		setMaxDamage(0);
		setMaxStackSize(0);
		setCreativeTab(TabRemoteIO.TAB);
	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return icon;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "tool");
	}
}
