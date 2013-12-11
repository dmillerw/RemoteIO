package com.dmillerw.remoteIO.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.block.tile.TileSideProxy;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.ChatHelper;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTool extends Item {

	public Icon icon;
	
	public ItemTool(int id) {
		super(id);
		
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			int id = world.getBlockId(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			
			if (id == BlockHandler.blockIOID && meta == 0) {
				TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);
				
				if (!player.isSneaking()) {
					if (!hasCoordinates(stack)) {
						ChatHelper.warn(player, "You must select a block to link with first");
						return false;
					} else {
						int[] coords = getCoordinates(stack);
						if (tile.setCoordinates(coords[0], coords[1], coords[2], coords[3])) {
							ChatHelper.info(player, "Linked");
						} else {
							ChatHelper.warn(player, "Check to ensure the IO block is within range and as the appropriate upgrades");
						}
						clearCoordinates(stack);
						return false;
					}
				} else {
					if (tile.hasCoordinates()) {
						tile.clearCoordinates();
						ChatHelper.info(player, "Cleared selected Remote IO's coordinates");
						return false;
					}
				}
			} else if (id == BlockHandler.blockIOID && meta == 1) {
				TileSideProxy tile = (TileSideProxy) world.getBlockTileEntity(x, y, z);
				
				if (!player.isSneaking()) {
					if (!hasCoordinates(stack)) {
						ChatHelper.warn(player, "You must select a block to link with first");
						world.markBlockForUpdate(x, y, z);
						return false;
					} else {
						int[] coords = getCoordinates(stack);
						tile.x = coords[0];
						tile.y = coords[1];
						tile.z = coords[2];
						tile.insertionSide = ForgeDirection.getOrientation(coords[4]);
						ChatHelper.info(player, "Linked");
						clearCoordinates(stack);
						world.notifyBlocksOfNeighborChange(x, y, z, BlockHandler.blockIOID);
						world.markBlockForUpdate(x, y, z);
						return false;
					}
				} else {
					if (tile.fullyValid()) {
						tile.x = 0;
						tile.y = -1;
						tile.z = 0;
						tile.insertionSide = ForgeDirection.UNKNOWN;
						ChatHelper.info(player, "Reset insertion side");
						world.notifyBlocksOfNeighborChange(x, y, z, BlockHandler.blockIOID);
						world.markBlockForUpdate(x, y, z);
						return false;
					}
				}
			} else {
				if (Block.blocksList[id] != null && Block.blocksList[id].hasTileEntity(meta)) {
					setCoordinates(stack, x, y, z, world.provider.dimensionId, side);
					ChatHelper.info(player, "Begun linking process");
					return false;
				} else {
					ChatHelper.warn(player, "You cannot link a Remote IO block to such a basic block");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && player.isSneaking() && hasCoordinates(stack)) {
			clearCoordinates(stack);
			ChatHelper.info(player, "Cleared link coordinates on linker tool");
		}
		
		return stack;
	}
	
	private void setCoordinates(ItemStack stack, int x, int y, int z, int d, int side) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		NBTTagCompound coords = new NBTTagCompound();
		
		coords.setInteger("x", x);
		coords.setInteger("y", y);
		coords.setInteger("z", z);
		coords.setInteger("d", d);
		coords.setInteger("side", side);
		
		nbt.setCompoundTag("coords", coords);
		
		stack.setTagCompound(nbt);
	}
	
	private int[] getCoordinates(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return new int[0];
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (!nbt.hasKey("coords")) {
			return new int[0];
		}
		
		NBTTagCompound coords = nbt.getCompoundTag("coords");
		
		return new int[] {coords.getInteger("x"), coords.getInteger("y"), coords.getInteger("z"), coords.getInteger("d"), coords.getInteger("side")};
	}
	
	private boolean hasCoordinates(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return false;
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (!nbt.hasKey("coords")) {
			return false;
		}
		
		return true;
	}
	
	private void clearCoordinates(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (nbt.hasKey("coords")) {
			nbt.removeTag("coords");
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIconFromDamage(int meta) {
		return this.icon;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "itemTool");
	}
	
}
