package remoteio.common.item;

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
import org.lwjgl.input.Keyboard;
import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.lib.DimensionalCoords;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.TileRemoteInventory;

import java.util.List;

public final class ItemRemoteAccessor
extends Item{
    private final IIcon[] icons = new IIcon[2];

    public ItemRemoteAccessor(){
        this.setMaxStackSize(1);
        this.setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void registerIcons(IIconRegister register){
        this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "accessor_inactive");
        this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "accessor_active");
    }

    @Override
    public IIcon getIconIndex(ItemStack stack){
        DimensionalCoords coords = getCoordinates(stack);
        if(coords != null){
            return icons[1];
        } else{
            return icons[0];
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass){
        DimensionalCoords coords = getCoordinates(stack);
        if(coords != null){
            return icons[1];
        } else{
            return icons[0];
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean debug) {
        DimensionalCoords coords = ItemRemoteAccessor.getCoordinates(stack);
        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            if (coords != null) {
                list.add("Dimension: " + DimensionManager.getProvider(coords.dimensionID).getDimensionName());
                list.add("X: " + coords.x + " Y: " + coords.y + " Z: " + coords.z);
            }
        } else{
            if(coords != null){
                list.add("Dimension: " + DimensionManager.getProvider(coords.dimensionID).getDimensionName());
                list.add("Block: " + player.worldObj.getBlock(coords.x, coords.y, coords.z).getLocalizedName());
            }
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz){
        if(getCoordinates(stack) != null){
            DimensionalCoords coords = getCoordinates(stack);
            RemoteIO.proxy.activateBlock(world, coords.x, coords.y, coords.z, player, side, fx, fy, fz);
            return true;
        }

        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
        if(!world.isRemote){
            TileEntity tile = world.getTileEntity(x, y, z);

            if(player.isSneaking() && !((tile instanceof TileRemoteInterface) || (tile instanceof TileRemoteInventory))){
                setCoordinates(stack, new DimensionalCoords(world.provider.dimensionId, x, y, z));
                player.addChatComponentMessage(new ChatComponentTranslation("chat.target.save"));
                return true;
            }
        }

        return false;
    }

    public static void setCoordinates(ItemStack stack, DimensionalCoords coords){
        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        coords.writeToNBT(tag);
        nbt.setTag("position", tag);
        stack.setTagCompound(nbt);
    }

    public static DimensionalCoords getCoordinates(ItemStack stack){
        if(!stack.hasTagCompound()){
            return null;
        }

        NBTTagCompound comp = stack.getTagCompound();

        if(!comp.hasKey("position")){
            return null;
        }

        return DimensionalCoords.fromNBT(comp.getCompoundTag("position"));
    }
}