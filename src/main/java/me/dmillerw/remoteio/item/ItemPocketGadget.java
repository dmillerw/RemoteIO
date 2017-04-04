package me.dmillerw.remoteio.item;

import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.core.frequency.DeviceRegistry;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.lib.ModTab;
import me.dmillerw.remoteio.network.PacketHandler;
import me.dmillerw.remoteio.network.packet.client.CActivateBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by dmillerw
 */
public class ItemPocketGadget extends Item {

    public static int getFrequency(ItemStack stack) {
        if (stack.isEmpty())
            return 0;

        if (stack.hasTagCompound())
            return stack.getTagCompound().getInteger("_frequency");
        else
            return 0;
    }

    public static void setFrequency(ItemStack stack, int frequency) {
        if (stack.isEmpty())
            return;

        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound tag = stack.getTagCompound();

        tag.setInteger("_frequency", frequency);

        stack.setTagCompound(tag);
    }

    public ItemPocketGadget() {
        super();

        setUnlocalizedName(ModInfo.MOD_ID + ":pocket_gadget");
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(ModTab.TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    	ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if (playerIn.isSneaking()) {
            playerIn.openGui(RemoteIO.instance, 0, playerIn.world, 0, -1, 0);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
        if (!worldIn.isRemote) {
            BlockPos pos = DeviceRegistry.getWatchedBlock(worldIn.provider.getDimension(), getFrequency(itemStackIn));
            if (pos != null) {
                boolean result = RemoteIO.proxy.onBlockActivated(worldIn, pos, worldIn.getBlockState(pos), playerIn, hand, null, 0, 0, 0);
                PacketHandler.INSTANCE.sendTo(new CActivateBlock(pos), (EntityPlayerMP) playerIn);
                if (result)
                    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, hand);
    }
}
