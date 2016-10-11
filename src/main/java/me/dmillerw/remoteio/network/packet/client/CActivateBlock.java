package me.dmillerw.remoteio.network.packet.client;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.network.PacketHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by dmillerw
 */
public class CActivateBlock implements IMessage {

    public BlockPos blockPos;

    public CActivateBlock() {

    }

    public CActivateBlock(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(blockPos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = BlockPos.fromLong(buf.readLong());
    }

    public static class Handler implements IMessageHandler<CActivateBlock, IMessage> {

        @Override
        public IMessage onMessage(final CActivateBlock message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> handleMessage(message, ctx));
            return null;
        }

        private void handleMessage(CActivateBlock message, MessageContext ctx) {
            RemoteIO.proxy.handleClientBlockActivationMessage(message); //TODO: Find a better way?
        }
    }
}
