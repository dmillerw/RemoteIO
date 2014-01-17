package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.core.helper.IOLogger;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

/**
 * Created by Dylan Miller on 1/16/14
 */
public class ConnectionHandler implements IConnectionHandler {

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager iNetworkManager) {
        try {
            PacketHandler.sendConfigData((EntityPlayer)player);
        } catch (IOException e) {
            IOLogger.warn("Failed to send config data to player: " + ((EntityPlayer) player).username);
            e.printStackTrace();
        }
    }

    @Override
    public String connectionReceived(NetLoginHandler netLoginHandler, INetworkManager iNetworkManager) {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netHandler, String s, int i, INetworkManager iNetworkManager) {

    }

    @Override
    public void connectionOpened(NetHandler netHandler, MinecraftServer minecraftServer, INetworkManager iNetworkManager) {

    }

    @Override
    public void connectionClosed(INetworkManager iNetworkManager) {
        PacketHandler.restoreFromCache();
    }

    @Override
    public void clientLoggedIn(NetHandler netHandler, INetworkManager iNetworkManager, Packet1Login packet1Login) {

    }

}
