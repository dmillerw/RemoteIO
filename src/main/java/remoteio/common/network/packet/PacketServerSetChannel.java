package remoteio.common.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.item.ItemWirelessLocationChip;
import remoteio.common.lib.ModItems;
import remoteio.common.tile.TileTransceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class PacketServerSetChannel implements IMessage, IMessageHandler<PacketServerSetChannel, IMessage> {

    public int x = 0;
    public int y = 0;
    public int z = 0;
    public int channel = 0;

    @Override
    public void toBytes(ByteBuf buf) {
        if (y > 0) {
            buf.writeBoolean(true);
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(channel);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }
        channel = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketServerSetChannel message, MessageContext ctx) {
        EntityPlayerMP entityPlayerMP = ctx.getServerHandler().playerEntity;
        if (message.y > 0) {
            TileTransceiver tileTransceiver = (TileTransceiver) entityPlayerMP.worldObj.getTileEntity(message.x, message.y, message.z);
            tileTransceiver.setChannel(message.channel);
        } else {
            ItemStack itemStack = entityPlayerMP.getCurrentEquippedItem();
            if (itemStack != null && itemStack.getItem() == ModItems.wirelessLocationChip) {
                ItemWirelessLocationChip.setChannel(itemStack, message.channel);
            }
        }
        return null;
    }
}
