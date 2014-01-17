package com.dmillerw.remoteIO.item;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.TileSideProxy;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.ChatHelper;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.List;

public class ItemTool extends Item {

	public Icon icon;
	
	public ItemTool(int id) {
		super(id);

        setMaxStackSize(1);
		this.setCreativeTab(CreativeTabRIO.tab);
	}

	@SuppressWarnings("unchecked")
    @Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idk) {
		if (hasCoordinates(stack)) {
			list.add("Coordinates Stored:");
			list.add("X: " + getCoordinates(stack)[0]);
			list.add("Y: " + getCoordinates(stack)[1]);
			list.add("Z: " + getCoordinates(stack)[2]);
		} else {
			list.add("No Coordinates Stored");
		}
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
			int id = world.getBlockId(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			
			if (id == BlockHandler.blockIOID) {
				TileSideProxy tile = (TileSideProxy) world.getBlockTileEntity(x, y, z);
				
				if (!player.isSneaking()) {
					if (!hasCoordinates(stack)) {
						ChatHelper.warn(player, "chat.fail.select");
						return true;
					} else {
						int[] coords = getCoordinates(stack);
                        tile.setCoordinates(coords[0], coords[1], coords[2], coords[3]);
                        ChatHelper.info(player, "chat.link.succeed");
						clearCoordinates(stack);
						return true;
					}
				} else {
					if (tile.connectionPosition() != null) {
						tile.clearCoordinates();
						ChatHelper.info(player, "chat.clear.io");
						return true;
					}
				}
			} else if (id == BlockHandler.blockProxyID) {
				TileSideProxy tile = (TileSideProxy) world.getBlockTileEntity(x, y, z);
				
				if (!player.isSneaking()) {
					if (!hasCoordinates(stack)) {
						ChatHelper.warn(player, "chat.fail.select");
						world.markBlockForUpdate(x, y, z);
						return false;
					} else {
						int[] coords = getCoordinates(stack);
                        tile.setCoordinates(coords[0], coords[1], coords[2], world.provider.dimensionId);
						tile.insertionSide = ForgeDirection.getOrientation(coords[4]);
						ChatHelper.info(player, "chat.link.succeed");
						clearCoordinates(stack);
						world.notifyBlocksOfNeighborChange(x, y, z, BlockHandler.blockIOID);
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				} else {
					if (tile.connectionPosition() != null) {
                        tile.setCoordinates(0, 0, 0, 0);
						tile.insertionSide = ForgeDirection.UNKNOWN;
						ChatHelper.info(player, "chat.clear.proxy");
						world.notifyBlocksOfNeighborChange(x, y, z, BlockHandler.blockIOID);
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			} else {
				if (Block.blocksList[id] != null && Block.blocksList[id].hasTileEntity(meta)) {
					setCoordinates(stack, x, y, z, world.provider.dimensionId, side);
					ChatHelper.info(player, "chat.link.start");
					return true;
				} else {
					ChatHelper.warn(player, "chat.fail.basic");
					return true;
				}
			}
		}
		
		return false;
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
		
		return nbt.hasKey("coords");
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
