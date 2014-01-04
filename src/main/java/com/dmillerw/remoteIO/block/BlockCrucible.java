package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.block.tile.TileCore;
import com.dmillerw.remoteIO.block.tile.TileCrucible;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

/**
 * Created by Dylan Miller on 1/3/14
 */
public class BlockCrucible extends BlockCore {

    private Icon[] icons;

    public BlockCrucible(int id) {
        super(id);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileCrucible tile = (TileCrucible) world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            if (side == tile.rotation.ordinal()) {
                return tile.isActive() ? this.icons[2] : this.icons[1];
            } else if (side == ForgeDirection.UP.ordinal()) {
                return this.icons[0];
            }
        }

        return this.icons[3];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        return side == ForgeDirection.UP.ordinal() ? this.icons[0] : side == ForgeDirection.WEST.ordinal() ? this.icons[1] : this.icons[3];
    }

    @Override
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[4];

        this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "crucible/top");
        this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "crucible/front_off");
        this.icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "crucible/front_on");
        this.icons[3] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
    }

    @Override
    public TileCore getTile(int meta) {
        return new TileCrucible();
    }

}
