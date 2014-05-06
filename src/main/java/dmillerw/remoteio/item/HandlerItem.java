package dmillerw.remoteio.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class HandlerItem {

	public static Item locationChip;
	public static Item transferChip;
	public static Item upgradeChip;

	public static void initialize() {
		locationChip = new ItemLocationChip().setUnlocalizedName("chip.location");
		GameRegistry.registerItem(locationChip, "remoteio:location_chip");

		transferChip = new ItemTransferChip().setUnlocalizedName("chip.transfer");
		GameRegistry.registerItem(transferChip, "remoteio:transfer_chip");
	}

}
