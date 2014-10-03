package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.helper.PlayerHelper;
import dmillerw.remoteio.lib.DimensionalCoords;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemWirelessTransmitter extends Item {

    public static boolean hasValidRemote(EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == HandlerItem.wirelessTransmitter) {
                if (player.getCommandSenderName().equalsIgnoreCase(getPlayerName(stack))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setHitSide(ItemStack stack, int side) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();

        nbt.setInteger("side", side);

        stack.setTagCompound(nbt);
    }

    public static void setHitCoordinates(ItemStack stack, float fx, float fy, float fz) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();

        NBTTagCompound tag = new NBTTagCompound();

        tag.setFloat("x", fx);
        tag.setFloat("y", fy);
        tag.setFloat("z", fz);

        nbt.setTag("hit", tag);

        stack.setTagCompound(nbt);
    }

    public static void setPlayer(ItemStack stack, EntityPlayer player) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();

        nbt.setString("player", player.getCommandSenderName());

        stack.setTagCompound(nbt);
    }

    public static int getHitSide(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return -1;
        }

        NBTTagCompound nbt = stack.getTagCompound();

        if (!nbt.hasKey("side")) {
            return -1;
        }

        return nbt.getInteger("side");
    }

    public static float[] getHitCoordinates(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return new float[]{0, 0, 0};
        }

        NBTTagCompound nbt = stack.getTagCompound();

        if (!nbt.hasKey("hit")) {
            return new float[]{0, 0, 0};
        }

        NBTTagCompound hit = nbt.getCompoundTag("hit");

        return new float[]{hit.getFloat("x"), hit.getFloat("y"), hit.getFloat("z")};
    }

    public static String getPlayerName(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();

        if (!nbt.hasKey("player")) {
            return null;
        }

        return nbt.getString("player");
    }

    public static EntityPlayer getPlayer(ItemStack stack) {
        String player = getPlayerName(stack);

        if (player != null && !player.isEmpty()) {
            return PlayerHelper.getPlayerForUsername(player);
        } else {
            return null;
        }
    }

    private IIcon icon;

    public ItemWirelessTransmitter() {
        super();

        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
        DimensionalCoords coords = ItemLocationChip.getCoordinates(stack);
        String bound = getPlayerName(stack);

        if (coords != null) {
            list.add("Dimension: " + DimensionManager.getProvider(coords.dimensionID).getDimensionName());
            list.add("X: " + coords.x + " Y: " + coords.y + " Z: " + coords.z);
            list.add(" --- ");
        }

        if (bound != null) {
            list.add("Bound to: " + bound);
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile != null && tile instanceof TileRemoteInterface) {
                ItemLocationChip.setCoordinates(stack, DimensionalCoords.create(tile));
                setHitSide(stack, side);
                setHitCoordinates(stack, hitX, hitY, hitZ);
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                setPlayer(stack, player);
                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.save"));
            } else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("position")) {
                DimensionalCoords coord = ItemLocationChip.getCoordinates(stack);
                int side = getHitSide(stack);
                float[] hit = getHitCoordinates(stack);

                if (coord.inWorld(world)) {
                    coord.getBlock().onBlockActivated(coord.getWorld(), coord.x, coord.y, coord.z, player, side, hit[0], hit[1], hit[2]);
                }
            }
        }

        return stack;
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "transmitter");
    }
}
