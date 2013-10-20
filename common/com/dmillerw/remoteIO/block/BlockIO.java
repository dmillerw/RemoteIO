package com.dmillerw.remoteIO.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.item.ItemUpgrade;
import com.dmillerw.remoteIO.item.Upgrade;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockIO extends BlockContainer {

	public Icon[] icons;
	
	public BlockIO(int id) {
		super(id, Material.iron);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			ItemStack held = player.getHeldItem();
			
			if (held != null && held.getItem() instanceof ItemUpgrade) {
				Upgrade upgrade = Upgrade.values()[held.getItemDamage()];
				TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);
				
				if (!tile.installedUpgrades.contains(upgrade)) {
					tile.installedUpgrades.add(upgrade);
					
					if (!player.capabilities.isCreativeMode) {
						if (--held.stackSize == 0) {
							held = null;
						}
						
						((EntityPlayerMP)player).updateHeldItem();
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			for (Upgrade upgrade : tile.installedUpgrades) {
				this.dropBlockAsItem_do(world, x, y, z, upgrade.toItemStack());
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		if (tile != null && tile.validCoordinates) {
			return this.icons[0];
		} else {
			return this.icons[1];
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[1];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIO");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIOInactive");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityIO();
	}
	
}
