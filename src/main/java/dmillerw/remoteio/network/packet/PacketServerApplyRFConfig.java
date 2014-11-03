package dmillerw.remoteio.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dmillerw.remoteio.core.TransferType;
import dmillerw.remoteio.lib.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public class PacketServerApplyRFConfig implements IMessage, IMessageHandler<PacketServerApplyRFConfig, IMessage> {

    public boolean pushPower;
    public int maxPushPower;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(pushPower);
        buf.writeInt(maxPushPower);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pushPower = buf.readBoolean();
        maxPushPower = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketServerApplyRFConfig message, MessageContext ctx) {
        EntityPlayerMP entityPlayerMP = ctx.getServerHandler().playerEntity;
        ItemStack itemStack = entityPlayerMP.getHeldItem();
        if (itemStack != null && itemStack.getItem() == ModItems.transferChip && itemStack.getItemDamage() == TransferType.ENERGY_RF) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (itemStack.hasTagCompound()) {
                nbtTagCompound = itemStack.getTagCompound();
            }
            nbtTagCompound.setBoolean("pushPower", message.pushPower);
            nbtTagCompound.setInteger("maxPushPower", message.maxPushPower);
            itemStack.setTagCompound(nbtTagCompound);
            entityPlayerMP.updateHeldItem();
        }
        return null;
    }
}
