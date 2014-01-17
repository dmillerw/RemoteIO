package com.dmillerw.remoteIO.core.handler;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.core.helper.IOLogger;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

/**
 * Created by Dylan Miller on 1/16/14
 */
public class PacketHandler implements IPacketHandler {

    /* ORIGINAL SETTINGS CACHE */
    public static int defaultRange = 8;

    public static int rangeUpgradeT1Boost = 8;
    public static int rangeUpgradeT2Boost = 16;
    public static int rangeUpgradeT3Boost = 64;
    public static int rangeUpgradeWitherBoost = 1024;

    public static void sendConfigData(EntityPlayer player) throws IOException {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            return;
        }

        IOLogger.info("Sending config data to " + player.username);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        dos.writeByte(0); // Packet ID

        dos.writeInt(RemoteIO.instance.defaultRange);
        dos.writeInt(RemoteIO.instance.rangeUpgradeT1Boost);
        dos.writeInt(RemoteIO.instance.rangeUpgradeT2Boost);
        dos.writeInt(RemoteIO.instance.rangeUpgradeT3Boost);
        dos.writeInt(RemoteIO.instance.rangeUpgradeWitherBoost);

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = ModInfo.ID;
        packet.isChunkDataPacket = false;
        packet.data = bos.toByteArray();
        packet.length = packet.data.length;
        PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
    }

    public static void handleConfigData(DataInputStream dis) throws IOException {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return;
        }

        IOLogger.info("Received config data from server");

        /* CACHE OLD VALUES */
        defaultRange = RemoteIO.instance.defaultRange;
        rangeUpgradeT1Boost = RemoteIO.instance.rangeUpgradeT1Boost;
        rangeUpgradeT2Boost = RemoteIO.instance.rangeUpgradeT2Boost;
        rangeUpgradeT3Boost = RemoteIO.instance.rangeUpgradeT3Boost;
        rangeUpgradeWitherBoost = RemoteIO.instance.rangeUpgradeWitherBoost;

        /* SET RECEIVED VALUES */
        RemoteIO.instance.defaultRange = dis.readInt();
        RemoteIO.instance.rangeUpgradeT1Boost = dis.readInt();
        RemoteIO.instance.rangeUpgradeT2Boost = dis.readInt();
        RemoteIO.instance.rangeUpgradeT3Boost = dis.readInt();
        RemoteIO.instance.rangeUpgradeWitherBoost = dis.readInt();
    }

    public static void restoreFromCache() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            return;
        }

        IOLogger.info("Restoring client configs");

        RemoteIO.instance.defaultRange = defaultRange;
        RemoteIO.instance.rangeUpgradeT1Boost = rangeUpgradeT1Boost;
        RemoteIO.instance.rangeUpgradeT2Boost = rangeUpgradeT2Boost;
        RemoteIO.instance.rangeUpgradeT3Boost = rangeUpgradeT3Boost;
        RemoteIO.instance.rangeUpgradeWitherBoost = rangeUpgradeWitherBoost;
    }

    @Override
    public void onPacketData(INetworkManager iNetworkManager, Packet250CustomPayload packet250CustomPayload, Player player) {
        ByteArrayInputStream bis = new ByteArrayInputStream(packet250CustomPayload.data);
        DataInputStream dis = new DataInputStream(bis);

        try {
            byte id = dis.readByte();

            if (id == 0) {
                try {
                    handleConfigData(dis);
                } catch(IOException ex) {
                    IOLogger.warn("Failed to read config settings sent from server!");
                    ex.printStackTrace();
                }
            }
        } catch(IOException ex) {
            IOLogger.warn("Failed to receive packet!");
            ex.printStackTrace();
        }
    }

}
