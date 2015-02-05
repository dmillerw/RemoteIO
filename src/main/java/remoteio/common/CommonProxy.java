package remoteio.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import remoteio.common.core.handler.ContainerHandler;
import remoteio.common.network.ServerProxyPlayer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void setClientPlayerSlot(int slot, ItemStack itemStack) {

    }

    public World getWorld(int dimension) {
        return MinecraftServer.getServer().worldServerForDimension(dimension);
    }

    public void activateBlock(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float fx, float fy, float fz) {
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
        Container container = entityPlayer.openContainer;
        ServerProxyPlayer proxyPlayer = new ServerProxyPlayer(entityPlayerMP);

        proxyPlayer.playerNetServerHandler = entityPlayerMP.playerNetServerHandler;
        proxyPlayer.inventory = entityPlayerMP.inventory;
        proxyPlayer.currentWindowId = entityPlayerMP.currentWindowId;
        proxyPlayer.inventoryContainer = entityPlayerMP.inventoryContainer;
        proxyPlayer.openContainer = entityPlayerMP.openContainer;
        proxyPlayer.worldObj = entityPlayerMP.worldObj;

        Block block = proxyPlayer.worldObj.getBlock(x, y, z);
        if (block != null) {
            proxyPlayer.theItemInWorldManager.activateBlockOrUseItem(proxyPlayer, proxyPlayer.worldObj, proxyPlayer.getHeldItem(), x, y, z, side, fx, fy, fz);
        }

        entityPlayerMP.theItemInWorldManager.thisPlayerMP = entityPlayerMP;
        if (container != proxyPlayer.openContainer) {
            entityPlayerMP.openContainer = proxyPlayer.openContainer;
        }

        ContainerHandler.INSTANCE.containerWhitelist.put(entityPlayerMP.getCommandSenderName(), entityPlayerMP.openContainer);
    }
}
