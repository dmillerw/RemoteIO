package me.dmillerw.remoteio.network.packet.client;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.RemoteIO;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.concurrent.Callable;

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
            final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            final World world = player.worldObj;

            Minecraft.getMinecraft().addScheduledTask(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    RemoteIO.proxy.onBlockActivated(
                            world,
                            message.blockPos,
                            world.getBlockState(message.blockPos),
                            player,
                            EnumHand.MAIN_HAND,
                            player.getHeldItem(EnumHand.MAIN_HAND),
                            null,
                            0, 0, 0
                    );
                    return null;
                }
            });

            return null;
        }
    }
}
