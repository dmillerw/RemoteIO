package remoteio.common.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import remoteio.common.core.TransferType;
import remoteio.common.lib.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author dmillerw
 */
public class PacketServerApplyRFConfig implements IMessage, IMessageHandler<PacketServerApplyRFConfig, IMessage> {

    public int maxPushRate;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(maxPushRate);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        maxPushRate = buf.readInt();
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
            nbtTagCompound.setInteger("maxPushRate", message.maxPushRate);
            itemStack.setTagCompound(nbtTagCompound);
            entityPlayerMP.updateHeldItem();
        }
        return null;
    }
}
