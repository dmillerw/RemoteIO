package me.dmillerw.remoteio.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.core.frequency.FrequencyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Created by dmillerw
 */
public class SFrequencyUpdate implements IMessage {

    public static final int TYPE_ADD = 0;
    public static final int TYPE_REMOVE = 1;

    protected byte type;
    public int frequency;
    @Nullable
    public String name;

    public SFrequencyUpdate() {

    }

    public SFrequencyUpdate(byte type, int frequency, String name) {
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

    public static class Handler implements IMessageHandler<SFrequencyUpdate, IMessage> {

        @Override
        public IMessage onMessage(final SFrequencyUpdate message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (message.type == TYPE_ADD) {
                        FrequencyRegistry.saveFrequency(message.frequency, message.name);
                    } else if (message.type == TYPE_REMOVE) {
                        FrequencyRegistry.deleteFrequency(message.frequency);
                    }
                    return null;
                }
            });
            return null;
        }
    }
}
