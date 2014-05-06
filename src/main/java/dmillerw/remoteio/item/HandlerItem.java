package dmillerw.remoteio.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class HandlerItem {

	public static Item locationChip;

	public static void initialize() {
		locationChip = new ItemLocationChip().setUnlocalizedName("location_chip");
		GameRegistry.registerItem(locationChip, "remoteio:location_chip");
	}

}
