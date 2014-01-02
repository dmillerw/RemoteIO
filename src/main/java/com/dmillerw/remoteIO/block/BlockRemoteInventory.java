package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileRemoteInventory;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRemoteInventory extends BlockContainer {

	public Icon[] icons;
	
	public BlockRemoteInventory(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        if (!world.isRemote) {
            ItemStack held = player.getCurrentEquippedItem();

            if (held == null || held.getItem() != ItemHandler.itemTransmitter) {
                player.openGui(RemoteIO.instance, 1, world, x, y, z);
                return true;
            }
        }

        return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileRemoteInventory tile = (TileRemoteInventory) world.getBlockTileEntity(x, y, z);

		if (side == 1) {
			if (tile != null && tile.lastClientState) {
				return this.icons[1];
			} else {
				return this.icons[0];
			}
		} else {
			return this.icons[2];
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return side == 1 ? this.icons[0] : this.icons[2];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[3];
		
		this.icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "remote/active");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "remote/inactive");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileRemoteInventory();
	}
	
}
