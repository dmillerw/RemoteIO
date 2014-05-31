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
	public static Item blankPlate;
	public static Item ioTool;
	public static Item wirelessTransmitter;

	public static void initialize() {
		locationChip = new ItemLocationChip().setUnlocalizedName("chip.location");
		GameRegistry.registerItem(locationChip, "remoteio:location_chip");

		transferChip = new ItemTransferChip().setUnlocalizedName("chip.transfer");
		GameRegistry.registerItem(transferChip, "remoteio:transfer_chip");

		upgradeChip = new ItemUpgradeChip().setUnlocalizedName("chip.upgrade");
		GameRegistry.registerItem(upgradeChip, "remoteio:upgrade_chip");

		blankPlate = new ItemBlankPlate().setUnlocalizedName("blank_plate");
		GameRegistry.registerItem(blankPlate, "remoteio:blank_plate");

		ioTool = new ItemIOTool().setUnlocalizedName("io_tool");
		GameRegistry.registerItem(ioTool, "remoteio:io_tool");

		wirelessTransmitter = new ItemWirelessTransmitter().setUnlocalizedName("wireless_transmitter");
		GameRegistry.registerItem(wirelessTransmitter, "remoteio:wireless_transmitter");
	}

}
