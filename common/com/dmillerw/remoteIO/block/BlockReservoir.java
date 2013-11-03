package com.dmillerw.remoteIO.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.dmillerw.remoteIO.block.render.RenderBlockReservoir;
import com.dmillerw.remoteIO.block.tile.TileEntityReservoir;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.StackHelper;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReservoir extends BlockContainer {

	public Icon iconFrame;
	public Icon iconFrameDark;
	public Icon iconGlass;
	
	public BlockReservoir(int id) {
		super(id, Material.rock);
		
		this.setHardness(5F);
		this.setResistance(1F);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
		ItemStack held = player.getHeldItem();
		IFluidHandler tank = (IFluidHandler) world.getBlockTileEntity(x, y, z);
		
		if (!player.isSneaking() && held != null && !world.isRemote) {
			FluidStack contained = tank.getTankInfo(ForgeDirection.getOrientation(side))[0].fluid;
			
			if (contained != null) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(contained, held);
				FluidStack toFill = FluidContainerRegistry.getFluidForFilledItem(filled);
				
				if (toFill != null) {
					if (held.stackSize > 1) {
						if (!player.inventory.addItemStackToInventory(filled)) {
							return false;
						} else {
							if (!player.capabilities.isCreativeMode) {
								if (--held.stackSize <= 0) {
									player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
								}
							}
						}
					} else {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, StackHelper.consumeItem(held));
						player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
					}
					
					tank.drain(ForgeDirection.getOrientation(side), toFill, true);
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityReservoir tile = (TileEntityReservoir) world.getBlockTileEntity(x, y, z);
		
		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntityReservoir tile = (TileEntityReservoir) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.onNeighborBlockUpdate();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return Block.waterStill.getIcon(side, meta);
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
		this.iconFrame = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrame");
		this.iconFrameDark = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirFrameDark");
		this.iconGlass = register.registerIcon(ModInfo.RESOURCE_PREFIX + "reservoirGlass");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return RenderBlockReservoir.renderID;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReservoir();
	}

}
