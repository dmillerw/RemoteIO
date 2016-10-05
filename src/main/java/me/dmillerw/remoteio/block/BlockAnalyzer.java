package me.dmillerw.remoteio.block;

import me.dmillerw.remoteio.RemoteIO;
import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.lib.ModTab;
import me.dmillerw.remoteio.tile.TileAnalyzer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class BlockAnalyzer extends Block implements ITileEntityProvider {

    public BlockAnalyzer() {
        super(Material.IRON);

        setUnlocalizedName(ModInfo.MOD_ID + ":block.analyzer");
        setDefaultState(this.blockState.getBaseState());
        setCreativeTab(ModTab.TAB);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAnalyzer();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (playerIn.isSneaking()) {
            playerIn.openGui(RemoteIO.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }
}
