package me.dmillerw.remoteio.block;

import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.lib.ModTab;
import me.dmillerw.remoteio.lib.property.UnlistedBoolean;
import me.dmillerw.remoteio.lib.property.UnlistedInteger;
import me.dmillerw.remoteio.lib.property.UnlistedString;
import me.dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by dmillerw
 */
public class BlockRemoteInterface extends Block implements ITileEntityProvider {

    public static final UnlistedString MIMICK_BLOCK = new UnlistedString("mimick_block");
    public static final UnlistedInteger MIMICK_VALUE = new UnlistedInteger("mimick_value");
    public static final UnlistedBoolean CAMOUFLAGE = new UnlistedBoolean("camouflage");

    public BlockRemoteInterface() {
        super(Material.IRON);

        setUnlocalizedName(ModInfo.MOD_ID + ":block.remote_interface");
        setDefaultState(this.blockState.getBaseState());
        setCreativeTab(ModTab.TAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] {MIMICK_BLOCK, MIMICK_VALUE, CAMOUFLAGE});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRemoteInterface();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            TileRemoteInterface remote = (TileRemoteInterface) tile;
            if (playerIn.isSneaking()) {
                playerIn.openGui(RemoteIO.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            } else {
                IBlockState connected = remote.getRemoteState();
                if (connected == null) {
                    return false;
                }

                return RemoteIO.proxy.onBlockActivated(worldIn, remote.getRemotePosition(), connected, playerIn, hand, null, side, hitX, hitY, hitZ);
            }
        }
        return false;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            return ((TileRemoteInterface)tile).getExtendedBlockState(state);
        } else {
            return super.getExtendedState(state, world, pos);
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            TileRemoteInterface remote = (TileRemoteInterface) tile;
            IBlockState connected = remote.getRemoteState();
            if (connected == null || (connected.getBlock() == Blocks.AIR || connected.getBlock() == this))
                return 0;

            return connected.getBlock().getLightValue(connected, world, remote.getRemotePosition());
        } else {
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            IBlockState connected = ((TileRemoteInterface)tile).getRemoteState();
            if (connected != null)
                connected.getBlock().randomDisplayTick(connected, worldIn, pos, rand);
        }
    }
}
