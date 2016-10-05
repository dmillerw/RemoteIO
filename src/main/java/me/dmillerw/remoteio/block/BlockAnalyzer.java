package me.dmillerw.remoteio.block;

import me.dmillerw.remoteio.lib.ModInfo;
import me.dmillerw.remoteio.lib.ModTab;
import me.dmillerw.remoteio.tile.TileAnalyzer;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
}
