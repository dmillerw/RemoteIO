package com.dmillerw.remoteIO.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileEntityIO;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemTool;
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

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		
		if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) {
			TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);
			tile.creativeMode = true;
		}
		
		onNeighborBlockChange(world, x, y, z, 0);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		if (!player.isSneaking() && (player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemTool))) {
			player.openGui(RemoteIO.instance, 0, world, x, y, z);
			return true;
		}
		
		return false;
	}
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.setRedstoneState(world.isBlockIndirectlyGettingPowered(x, y, z));
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			for (ItemStack stack : InventoryHelper.getContents(tile.upgrades)) {
				if (stack != null) this.dropBlockAsItem_do(world, x, y, z, stack);
			}
		}
		
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileEntityIO tile = (TileEntityIO) world.getBlockTileEntity(x, y, z);

		ItemStack camo = tile.camo.getStackInSlot(0);
		Block block = null;
		if (camo != null && camo.itemID < 4096) {
			block = Block.blocksList[camo.itemID];
		}
		
		if (block != null && block.renderAsNormalBlock() && tile.hasUpgrade(Upgrade.CAMO)) {
			return block.getIcon(side, camo.getItemDamage());
		} else {
			if (tile != null && tile.validCoordinates) {
				return this.icons[0];
			} else {
				return this.icons[1];
			}
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
