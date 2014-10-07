package dmillerw.remoteio.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.network.packet.PacketClientForceSlot;
import dmillerw.remoteio.network.packet.PacketClientWhitelist;

/**
 * @author dmillerw
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.ID);

    public static void initialize() {
        INSTANCE.registerMessage(PacketClientWhitelist.class, PacketClientWhitelist.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(PacketClientForceSlot.class, PacketClientForceSlot.class, 1, Side.CLIENT);
    }
}
