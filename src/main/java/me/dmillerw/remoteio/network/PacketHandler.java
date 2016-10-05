package me.dmillerw.remoteio.network;

import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.network.packet.client.CActivateBlock;
import me.dmillerw.remoteio.network.packet.client.CBulkFrequencyUpdate;
import me.dmillerw.remoteio.network.packet.client.CFrequencyUpdate;
import me.dmillerw.remoteio.network.packet.server.SFrequencyUpdate;
import me.dmillerw.remoteio.network.packet.server.SSetFrequency;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by dmillerw
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MOD_ID);

    public static void initialize() {
        INSTANCE.registerMessage(CActivateBlock.Handler.class, CActivateBlock.class, -1, Side.CLIENT);
        INSTANCE.registerMessage(CBulkFrequencyUpdate.Handler.class, CBulkFrequencyUpdate.class, -2, Side.CLIENT);
        INSTANCE.registerMessage(CFrequencyUpdate.Handler.class, CFrequencyUpdate.class, -3, Side.CLIENT);

        INSTANCE.registerMessage(SFrequencyUpdate.Handler.class, SFrequencyUpdate.class, 1, Side.SERVER);
        INSTANCE.registerMessage(SSetFrequency.Handler.class, SSetFrequency.class, 2, Side.SERVER);
    }
}
