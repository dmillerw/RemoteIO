package dmillerw.remoteio.block;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileMachineHeater;
import dmillerw.remoteio.tile.TileMachineReservoir;
import dmillerw.remoteio.tile.core.TileCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import java.util.List;

/**
 * @author dmillerw
 */
public class BlockMachine extends BlockContainer {

    public static IIcon[] icons;
    public static IIcon[] overlays;

    public BlockMachine() {
        super(Material.iron);

        setHardness(5F);
        setResistance(5F);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            TileCore tile = (TileCore) world.getTileEntity(x, y, z);
            if (tile != null) tile.onNeighborUpdated();
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            ItemStack held = player.getHeldItem();
            if (held != null) {
                if (held.stackSize == 1) {
                    if (held.getItem() instanceof IFluidContainerItem) {
                        if (!world.isRemote) {
                            IFluidContainerItem fluidContainerItem = (IFluidContainerItem) held.getItem();
                            fluidContainerItem.fill(held, new FluidStack(FluidRegistry.WATER, fluidContainerItem.getCapacity(held)), true);
                        }
                        return true;
                    } else if (FluidContainerRegistry.isEmptyContainer(held)) {
                        if (!world.isRemote) {
                            ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.getContainerCapacity(held)), held);
                            if (filled != null) {
                                player.setCurrentItemOrArmor(0, filled);
                                return true;
                            }
                        }
                        return false;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile != null) {
            if (tile instanceof TileMachineReservoir) {
                return ((TileMachineReservoir) tile).filled ? icons[0] : icons[2];
            } else if (tile instanceof TileMachineHeater) {
                return ((TileMachineHeater) tile).filled ? icons[1] : icons[2];
            }
        }

        return getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[3];
        icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoir");
        icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "heater");
        icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "machine_inactive");
        overlays = new IIcon[2];
        overlays[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/reservoir");
        overlays[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/heater");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (meta) {
            case 0:
                return new TileMachineReservoir();
            case 1:
                return new TileMachineHeater();
            default:
                return null;
        }
    }
}
