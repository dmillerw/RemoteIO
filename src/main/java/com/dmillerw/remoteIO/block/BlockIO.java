package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.block.tile.TileSideProxy;
import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class BlockIO extends BlockIOCore {

	public Icon[] icons;

    public BlockIO(int id) {
        super(id);
    }

    @Override
    public TileIOCore getTile() {
        return new TileSideProxy();
    }

    @Override
    public int getGuiID() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[meta];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "io/active");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "io/inactive");
	}

}
