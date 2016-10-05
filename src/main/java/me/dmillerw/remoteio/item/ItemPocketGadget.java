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
        return 0; // TEMP
    }

    public static void setFrequency(ItemStack stack, int frequency) {

    }

    public ItemPocketGadget() {
        super();

        setUnlocalizedName(ModInfo.MOD_ID + ":pocket_gadget");
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(ModTab.TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (playerIn.isSneaking()) {
            playerIn.openGui(RemoteIO.instance, 0, playerIn.worldObj, 0, 0, 0);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
        if (!worldIn.isRemote) {
            BlockPos pos = DeviceRegistry.getWatchedBlock(getFrequency(itemStackIn));
            if (pos != null) {
                boolean result = RemoteIO.proxy.onBlockActivated(worldIn, pos, worldIn.getBlockState(pos), playerIn, hand, itemStackIn, null, 0, 0, 0);
                PacketHandler.INSTANCE.sendTo(new CActivateBlock(pos), (EntityPlayerMP) playerIn);
                if (result)
                    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
            }
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }
}
