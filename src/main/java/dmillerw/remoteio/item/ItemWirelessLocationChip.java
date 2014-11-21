package dmillerw.remoteio.item;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ItemWirelessLocationChip extends Item {

    public static int getChannel(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound().getInteger("channel");
    }

    public static void setChannel(ItemStack itemStack, int channel) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
        nbtTagCompound.setInteger("channel", channel);
        itemStack.setTagCompound(nbtTagCompound);
    }

    private IIcon icon;

    public ItemWirelessLocationChip() {
        super();

        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (entityPlayer.isSneaking()) {
            entityPlayer.openGui(RemoteIO.instance, GuiHandler.GUI_SET_CHANNEL, world, 0, 0, 0);
        }
        return itemStack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                RemoteIO.channelRegistry.setChannelData(getChannel(stack), new DimensionalCoords(world.provider.dimensionId, x, y, z));
                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.save"));
            }
        }

        return !world.isRemote;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }
}
