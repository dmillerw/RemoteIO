package dmillerw.remoteio.block;

import cpw.mods.fml.common.registry.GameRegistry;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.block.Block;

/**
 * @author dmillerw
 */
public class HandlerBlock {

	public static Block remoteInterface;
	public static Block remoteInventory;

	public static void initialize() {
		remoteInterface = new BlockRemoteInterface().setBlockName("remote_interface");
		GameRegistry.registerBlock(remoteInterface, remoteInterface.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileRemoteInterface.class, remoteInterface.getUnlocalizedName());

		remoteInventory = new BlockRemoteInventory().setBlockName("remote_inventory");
		GameRegistry.registerBlock(remoteInventory, remoteInventory.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileRemoteInventory.class, remoteInventory.getUnlocalizedName());
	}
}
