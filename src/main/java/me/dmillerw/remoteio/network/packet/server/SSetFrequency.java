package me.dmillerw.remoteio.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import me.dmillerw.remoteio.item.ItemPocketGadget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class SSetFrequency implements IMessage {

    protected int frequency;
    @Nullable
    protected BlockPos tilePos;

    public SSetFrequency() {

    }

    public SSetFrequency(int frequency, BlockPos tilePos) {
        this.frequency = frequency;
        this.tilePos = tilePos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(frequency);
        if (tilePos != null) {
            buf.writeBoolean(true);
            buf.writeLong(tilePos.toLong());
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        frequency = buf.readInt();
        if (buf.readBoolean())
            tilePos = BlockPos.fromLong(buf.readLong());
    }

    public static class Handler implements IMessageHandler<SSetFrequency, IMessage> {

        @Override
        public IMessage onMessage(SSetFrequency message, MessageContext ctx) {
            if (message.tilePos == null) {
                ItemPocketGadget.setFrequency(ctx.getServerHandler().playerEntity.getActiveItemStack(), message.frequency);
            } else {
                World world = ctx.getServerHandler().playerEntity.worldObj;
                IFrequencyProvider provider = (IFrequencyProvider) world.getTileEntity(message.tilePos);

                if (provider != null)
                    provider.setFrequency(message.frequency);
            }
            return null;
        }
    }
}