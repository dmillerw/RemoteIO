package remoteio.common.lib;

import cpw.mods.fml.common.registry.GameRegistry;
import remoteio.common.item.*;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class ModItems {
    public static Item locationChip;
    public static Item transferChip;
    public static Item upgradeChip;
    public static Item blankPlate;
    public static Item ioTool;
    public static Item wirelessTransmitter;
    public static Item interactionInhibitor;
    public static Item wirelessLocationChip;
    public static Item pda;
    public static Item remoteAccessor;
    public static Item linker;

    public static void initialize() {
        locationChip = new ItemLocationChip().setUnlocalizedName("chip.location");
        register(locationChip);

        transferChip = new ItemTransferChip().setUnlocalizedName("chip.transfer");
        register(transferChip);

        upgradeChip = new ItemUpgradeChip().setUnlocalizedName("chip.upgrade");
        register(upgradeChip);

        blankPlate = new ItemBlankPlate().setUnlocalizedName("blank_plate");
        register(blankPlate);

        ioTool = new ItemIOTool().setUnlocalizedName("io_tool");
        register(ioTool);

        wirelessTransmitter = new ItemWirelessTransmitter().setUnlocalizedName("wireless_transmitter");
        register(wirelessTransmitter);

        pda = new ItemPDA().setUnlocalizedName("pda");
        register(pda);

        remoteAccessor = new ItemRemoteAccessor().setUnlocalizedName("remoteAccessor");
        register(remoteAccessor);

        linker = new ItemLinker().setUnlocalizedName("linker");
        register(linker);
    }

    private static void register(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName());
    }
}
