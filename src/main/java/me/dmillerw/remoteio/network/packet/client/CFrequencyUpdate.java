package me.dmillerw.remoteio.network.packet.client;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.client.gui.GuiFrequency;
import me.dmillerw.remoteio.network.PacketHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class CFrequencyUpdate implements IMessage {

    public static final int TYPE_ADD = 0;
    public static final int TYPE_REMOVE = 1;

    protected byte type;
    public int frequency;
    @Nullable
    public String name;

    public CFrequencyUpdate() {

    }

    public CFrequencyUpdate(byte type, int frequency, String name) {
        this.type = type;
        this.frequency = frequency;
        this.name = name;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type);
        buf.writeInt(frequency);
        if (type == TYPE_ADD)
            ByteBufUtils.writeUTF8String(buf, name);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readByte();
        frequency = buf.readInt();
        if (type == TYPE_ADD)
            name = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<CFrequencyUpdate, IMessage> {

        @Override
        public IMessage onMessage(CFrequencyUpdate message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> handleMessage(message, ctx));
            return null;
        }

        private void handleMessage(CFrequencyUpdate message, MessageContext ctx) {
            if (message.type == TYPE_ADD) {
                GuiFrequency.frequencies.put(message.frequency, message.name);
            } else if (message.type == TYPE_REMOVE) {
                GuiFrequency.frequencies.remove(message.frequency);
            }
        }
    }
}
