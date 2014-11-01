package dmillerw.remoteio.core.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.remoteio.client.render.*;
import dmillerw.remoteio.tile.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    public static boolean allowContainerUsage = false;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerBlockHandler(new RenderBlockRemoteInterface());

        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInterface.class, new RenderTileRemoteInterface());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRemoteInventory.class, new RenderTileRemoteInventory());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineReservoir.class, new RenderTileMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileMachineHeater.class, new RenderTileMachine());
        ClientRegistry.bindTileEntitySpecialRenderer(TileIntelligentWorkbench.class, new RenderTileIntelligentWorkbench());
    }

    @Override
    public boolean canPlayerOpenContainer(EntityPlayer player) {
        return allowContainerUsage;
    }

    @Override
    public void resetPlayerWhitelist(EntityPlayer player) {
        allowContainerUsage = false;
    }
}
