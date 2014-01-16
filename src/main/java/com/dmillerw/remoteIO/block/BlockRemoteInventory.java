package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.block.tile.TileRemoteInventory;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public class BlockRemoteInventory extends BlockIOCore {

	public Icon[] icons;

    public BlockRemoteInventory(int id) {
        super(id);
    }

    @Override
    public TileIOCore getTile() {
        return new TileRemoteInventory();
    }

    @Override
    public int getGuiID() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return side == 1 ? this.icons[meta] : this.icons[2];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[3];
		
		this.icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "remote/active");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "remote/inactive");
	}

}
