package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.block.tile.TileSideProxy;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class BlockSideProxy extends BlockIOCore {

	public static float MIN = 0.1875F;
	public static float MAX = 0.8125F;
	
	public Icon[] icons;
	
	public BlockSideProxy(int id) {
		super(id);
		
		this.setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
	}

    @Override
    public TileIOCore getTile() {
        return new TileSideProxy();
    }

    @Override
    public int getGuiID() {
        return 4;
    }

    @SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[meta];
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "other/active");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "other/inactive");
	}
	
}
