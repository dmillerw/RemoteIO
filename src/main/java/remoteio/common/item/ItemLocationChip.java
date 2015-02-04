package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;

import java.util.List;

/**
 * @author dmillerw
 */
public final class ItemLocationChip
extends Item {
    public ItemLocationChip() {
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
        DimensionalCoords coords = ItemLocationChip.getCoordinates(stack);
        if (coords != null) {
            list.add("Dimension: " + DimensionManager.getProvider(coords.dimensionID).getDimensionName());
            list.add("X: " + coords.x + " Y: " + coords.y + " Z: " + coords.z);
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (player.isSneaking()) {
                ItemLocationChip.setCoordinates(stack, new DimensionalCoords(world.provider.dimensionId, x, y, z));
                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.save"));
                return true;
            } else {
                if (tile != null) {
                    if (tile instanceof TileRemoteInterface && !((TileRemoteInterface) tile).locked) {
                        DimensionalCoords coords = ItemLocationChip.getCoordinates(stack);

                        if (coords != null) {
                                ((TileRemoteInterface) tile).setRemotePosition(coords);
                                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.load"));
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    public static void setCoordinates(ItemStack stack, DimensionalCoords coords) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        coords.writeToNBT(tag);
        nbt.setTag("position", tag);
        stack.setTagCompound(nbt);
    }

    public static DimensionalCoords getCoordinates(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }

        NBTTagCompound nbt = stack.getTagCompound();

        if (!nbt.hasKey("position")) {
            return null;
        }

        return DimensionalCoords.fromNBT(nbt.getCompoundTag("position"));
    }
}
