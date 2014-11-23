package dmillerw.remoteio.core.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dmillerw.remoteio.client.documentation.Documentation;
import dmillerw.remoteio.client.handler.SoundHandler;
import dmillerw.remoteio.client.handler.TooltipEventHandler;
import dmillerw.remoteio.client.render.*;
import dmillerw.remoteio.core.helper.EventHelper;
import dmillerw.remoteio.network.ClientProxyPlayer;
import dmillerw.remoteio.tile.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

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
        ClientRegistry.bindTileEntitySpecialRenderer(TileIntelligentWorkbench.class, new RenderTileIntelligentWorkbench());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, new RenderTileTransceiver());

//        EventHelper.register(SoundHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
        EventHelper.register(new TooltipEventHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        Documentation.initialize();
    }

    @Override
    public void setClientPlayerSlot(int slot, ItemStack itemStack) {
        Minecraft.getMinecraft().thePlayer.openContainer.getSlot(slot).putStack(itemStack);
    }

    @Override
    public World getWorld(int dimension) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return super.getWorld(dimension);
        } else {
            return Minecraft.getMinecraft().theWorld;
        }
    }

    @Override
    public void activateBlock(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float fx, float fy, float fz) {
        if (entityPlayer instanceof EntityPlayerMP) {
            super.activateBlock(world, x, y, z, entityPlayer, side, fx, fy, fz);
        } else {
            EntityClientPlayerMP entityClientPlayerMP = (EntityClientPlayerMP) entityPlayer;
            ClientProxyPlayer proxyPlayer = new ClientProxyPlayer(entityClientPlayerMP);
            proxyPlayer.inventory = entityClientPlayerMP.inventory;
            proxyPlayer.inventoryContainer = entityClientPlayerMP.inventoryContainer;
            proxyPlayer.openContainer = entityClientPlayerMP.openContainer;
            proxyPlayer.movementInput = entityClientPlayerMP.movementInput;

            Block block = entityClientPlayerMP.worldObj.getBlock(x, y, z);
            if (block != null) {
                SoundHandler.INSTANCE.translateNextSound(x, y, z);

                if (proxyPlayer.getHeldItem() != null) {
                    if (proxyPlayer.getHeldItem().getItem().onItemUseFirst(proxyPlayer.getHeldItem(), proxyPlayer, proxyPlayer.worldObj, x, y, z, side, fx, fy, fz))
                        return;
                }
                block.onBlockActivated(entityClientPlayerMP.worldObj, x, y, z, proxyPlayer, side, fx, fy, fz);
            }

            if (entityClientPlayerMP.openContainer != proxyPlayer.openContainer) {
                entityClientPlayerMP.openContainer = proxyPlayer.openContainer;
            }
        }
    }
}
