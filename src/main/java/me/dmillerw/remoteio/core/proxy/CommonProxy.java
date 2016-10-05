package me.dmillerw.remoteio.core.proxy;

import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import me.dmillerw.remoteio.core.handler.ContainerHandler;
import me.dmillerw.remoteio.core.handler.PlayerEventHandler;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.network.GuiHandler;
import me.dmillerw.remoteio.network.PacketHandler;
import me.dmillerw.remoteio.network.player.ServerProxyPlayer;
import me.dmillerw.remoteio.tile.TileAnalyzer;
import me.dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileRemoteInterface.class, ModInfo.MOD_ID + ".remote_interface");
        GameRegistry.registerTileEntity(TileAnalyzer.class, ModInfo.MOD_ID + ".analyzer");

        NetworkRegistry.INSTANCE.registerGuiHandler(RemoteIO.instance, new GuiHandler());

        ContainerHandler.initialize();
        PacketHandler.initialize();
        PlayerEventHandler.initialize();

        MinecraftForge.EVENT_BUS.register(DeviceRegistry.TickHandler.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
        Container container = entityPlayer.openContainer;
        ServerProxyPlayer proxyPlayer = new ServerProxyPlayer(entityPlayerMP);

        proxyPlayer.connection = entityPlayerMP.connection;
        proxyPlayer.inventory = entityPlayerMP.inventory;
        proxyPlayer.currentWindowId = entityPlayerMP.currentWindowId;
        proxyPlayer.inventoryContainer = entityPlayerMP.inventoryContainer;
        proxyPlayer.openContainer = entityPlayerMP.openContainer;
        proxyPlayer.worldObj = entityPlayerMP.worldObj;

        EnumActionResult result = proxyPlayer.interactionManager.processRightClickBlock(proxyPlayer, world, heldItem, hand, pos, side, hitX, hitY, hitZ);

        entityPlayerMP.interactionManager.thisPlayerMP = entityPlayerMP;
        if (container != proxyPlayer.openContainer) {
            entityPlayerMP.openContainer = proxyPlayer.openContainer;
        }

        ContainerHandler.INSTANCE.containerWhitelist.put(
                entityPlayerMP.getDisplayNameString(),
                entityPlayerMP.openContainer);

        return result == EnumActionResult.SUCCESS;
    }
}
