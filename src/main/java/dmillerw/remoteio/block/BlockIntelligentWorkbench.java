package dmillerw.remoteio.block;

import dmillerw.remoteio.RemoteIO;
import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.handler.GuiHandler;
import dmillerw.remoteio.core.helper.InventoryHelper;
import dmillerw.remoteio.lib.ModInfo;
import dmillerw.remoteio.tile.TileIntelligentWorkbench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockIntelligentWorkbench extends BlockContainer {

    public static IIcon blank;
    public static IIcon top;
    public static IIcon overlay;

    public BlockIntelligentWorkbench() {
        super(Material.iron);

        setHardness(5F);
        setResistance(5F);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        player.openGui(RemoteIO.instance, GuiHandler.GUI_INTELLIGENT_WORKBENCH, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileIntelligentWorkbench tile = (TileIntelligentWorkbench) world.getTileEntity(x, y, z);

            if (tile != null) {
                InventoryHelper.dropContents(tile.craftMatrix, world, x, y, z);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        blank = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
        top = register.registerIcon(ModInfo.RESOURCE_PREFIX + "workbench");
        overlay = register.registerIcon(ModInfo.RESOURCE_PREFIX + "overlay/workbench");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? top : blank;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileIntelligentWorkbench();
    }
}
