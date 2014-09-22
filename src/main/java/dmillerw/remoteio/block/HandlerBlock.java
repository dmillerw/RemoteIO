package dmillerw.remoteio.block;

import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.item.block.ItemBlockMulti;
import dmillerw.remoteio.tile.TileMachineHeater;
import dmillerw.remoteio.tile.TileMachineReservoir;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.block.Block;

/**
 * @author dmillerw
 */
public class HandlerBlock {

    public static Block remoteInterface;
    public static Block remoteInventory;
    public static Block machine;
    public static Block skylight;

    public static void initialize() {
        remoteInterface = new BlockRemoteInterface().setBlockName("remote_interface");
        GameRegistry.registerBlock(remoteInterface, remoteInterface.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileRemoteInterface.class, remoteInterface.getUnlocalizedName());

        remoteInventory = new BlockRemoteInventory().setBlockName("remote_inventory");
        GameRegistry.registerBlock(remoteInventory, remoteInventory.getUnlocalizedName());
        GameRegistry.registerTileEntity(TileRemoteInventory.class, remoteInventory.getUnlocalizedName());

        machine = new BlockMachine().setBlockName("machine");
        GameRegistry.registerBlock(machine, ItemBlockMulti.class, machine.getUnlocalizedName(), new Object[]{new String[]{"reservoir", "heater"}});
        GameRegistry.registerTileEntity(TileMachineReservoir.class, "remoteio:machine_reservoir");
        GameRegistry.registerTileEntity(TileMachineHeater.class, "remoteio:machine_heater");

        skylight = new BlockSkylight().setBlockName("skylight");
        GameRegistry.registerBlock(skylight, "skylight");
    }
}
