package me.dmillerw.remoteio.network.packet.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import me.dmillerw.remoteio.item.ItemPocketGadget;
import me.dmillerw.remoteio.item.ModItems;
import me.dmillerw.remoteio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
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
            PacketHandler.addScheduledTask(ctx.netHandler, () -> handleMessage(message, ctx));
            return null;
        }

        private void handleMessage(SSetFrequency message, MessageContext ctx) {
            if (message.tilePos == null) {
                EntityPlayer player = ctx.getServerHandler().playerEntity;
                ItemStack active = player.getHeldItemMainhand();
                if (active != null && active.getItem() == ModItems.pocket_gadget) {
                    ItemPocketGadget.setFrequency(active, message.frequency);
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, active);
                }
            } else {
                World world = ctx.getServerHandler().playerEntity.worldObj;
                IFrequencyProvider provider = (IFrequencyProvider) world.getTileEntity(message.tilePos);

                if (provider != null)
                    provider.setFrequency(message.frequency);
            }
        }
    }
}
