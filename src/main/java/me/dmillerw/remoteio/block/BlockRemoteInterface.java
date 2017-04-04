package me.dmillerw.remoteio.block;

import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.lib.ModTab;
import me.dmillerw.remoteio.lib.property.RenderStateProperty;
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
import net.minecraft.util.math.AxisAlignedBB;
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

    public static final RenderStateProperty RENDER_STATE = new RenderStateProperty("render_state");

    public BlockRemoteInterface() {
        super(Material.IRON);

        setUnlocalizedName(ModInfo.MOD_ID + ":block.remote_interface");
        setDefaultState(this.blockState.getBaseState());
        setCreativeTab(ModTab.TAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] {RENDER_STATE});
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

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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

                RemoteIO.proxy.onBlockActivated(worldIn, remote.getRemotePosition(), connected, playerIn, hand, side, hitX, hitY, hitZ);

                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            TileRemoteInterface remote = (TileRemoteInterface) tile;
            IBlockState connected = remote.getRemoteState();

            if (connected != null)
                connected.getBlock().onBlockClicked(worldIn, remote.getRemotePosition(), playerIn);
        }
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = source.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            TileRemoteInterface remote = (TileRemoteInterface) tile;
            IBlockState connected = remote.getRemoteState();

            if (connected != null) {
                return connected.getBlock().getBoundingBox(connected, source, remote.getRemotePosition());
            }
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    	TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            TileRemoteInterface remote = (TileRemoteInterface) tile;
            IBlockState connected = remote.getRemoteState();

            if (connected != null) {
                return connected.getBlock().getCollisionBoundingBox(connected, worldIn, remote.getRemotePosition());
            }
        }
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
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
        //TODO: Disabled until I can find a way to shift particles spawned in world
        /*TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileRemoteInterface) {
            IBlockState connected = ((TileRemoteInterface)tile).getRemoteState();
            if (connected != null)
                connected.getBlock().randomDisplayTick(connected, worldIn, pos, rand);
        }*/
    }
}
