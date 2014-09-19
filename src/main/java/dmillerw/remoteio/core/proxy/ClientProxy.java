package dmillerw.remoteio.core.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.remoteio.client.render.RenderBlockRemoteInterface;
import dmillerw.remoteio.client.render.RenderTileMachine;
import dmillerw.remoteio.client.render.RenderTileRemoteInterface;
import dmillerw.remoteio.client.render.RenderTileRemoteInventory;
import dmillerw.remoteio.tile.TileMachineHeater;
import dmillerw.remoteio.tile.TileMachineReservoir;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerBlockHandler(new RenderBlockRemoteInterface());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInterface.class, new RenderTileRemoteInterface());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInventory.class, new RenderTileRemoteInventory());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineReservoir.class, new RenderTileMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineHeater.class, new RenderTileMachine());
    }
}
