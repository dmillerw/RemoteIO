package remoteio.common.item;

import remoteio.common.RemoteIO;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.core.TransferType;
import remoteio.common.core.handler.GuiHandler;
import remoteio.common.lib.ModInfo;
import remoteio.common.tile.TileRemoteInterface;
import remoteio.common.tile.core.TileIOCore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemTransferChip extends ItemSelectiveMeta {

    private IIcon[] icons;

    public ItemTransferChip() {
        super(new int[]{
                TransferType.MATTER_ITEM,
                TransferType.MATTER_FLUID,
                TransferType.MATTER_ESSENTIA,

                TransferType.ENERGY_IC2,
                TransferType.ENERGY_RF,

                TransferType.NETWORK_AE,

                TransferType.REDSTONE
        },

                new String[]{
                        "item",
                        "fluid",
                        "essentia",

                        "energy_ic2",
                        "energy_rf",

                        "network_ae",

                        "redstone"
                });

        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean debug) {
        if (itemStack.getItemDamage() == TransferType.ENERGY_RF) {
            if (itemStack.hasTagCompound()) {
                list.add("Push Power: " + itemStack.getTagCompound().getBoolean("pushPower"));
                list.add("Power Rate: " + itemStack.getTagCompound().getInteger("maxPushRate"));
            }
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile != null && tile instanceof TileIOCore) {
                TileIOCore io = (TileIOCore) tile;

                if (!(io instanceof TileRemoteInterface) || !((TileRemoteInterface) io).locked) {
                    ItemStack chip = stack.copy();
                    chip.stackSize = 1;

                    if (TileEntityHopper.func_145889_a(io.transferChips, chip, ForgeDirection.UNKNOWN.ordinal()) == null) {
                        io.callback(io.transferChips);
                        if (stack.stackSize == 1) {
                            player.setCurrentItemOrArmor(0, null);
                        } else {
                            ItemStack stack1 = stack.copy();
                            stack1.stackSize = stack.stackSize - 1;
                            player.setCurrentItemOrArmor(0, stack1);
                        }
                        return true;
                    }

                    if (io instanceof TileRemoteInterface)
                        ((TileRemoteInterface) io).updateRemotePosition();
                    io.updateVisualState();
                    io.markForUpdate();
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (world.isRemote && entityPlayer.isSneaking()) {
            if (itemStack.getItemDamage() == TransferType.ENERGY_RF)
                entityPlayer.openGui(RemoteIO.instance, GuiHandler.GUI_RF_CONFIG, world, 0, 0, 0);
        }
        return itemStack;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 1) {
            return names.get(stack.getItemDamage()).hashCode();
        }
        return 0xFFFFFF;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? icons[1] : icons[0];
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[2];
        icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "chip");
        icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "plate_transfer");
    }
}
