package dmillerw.remoteio.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.remoteio.core.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;

/**
 * @author dmillerw
 */
public class PacketClientWhitelist implements IMessage, IMessageHandler<PacketClientWhitelist, IMessage> {

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(PacketClientWhitelist message, MessageContext ctx) {
        ClientProxy.allowContainerUsage = true;
        return null;
    }
}
