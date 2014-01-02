package com.dmillerw.remoteIO.item;

import com.dmillerw.remoteIO.block.BlockHandler;
import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.block.tile.TileRemoteInventory;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.ChatHelper;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ItemTransmitter extends Item {

	public static boolean hasSelfRemote(EntityPlayer player) {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack != null) {
				if (stack.getItem() == ItemHandler.itemTransmitter) {
					if (stack.hasTagCompound() && (stack.getTagCompound().hasKey("player") && stack.getTagCompound().getString("player").equals(player.username))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Icon icon;
	
	public ItemTransmitter(int id) {
		super(id);
		
		setMaxStackSize(1);
		setCreativeTab(CreativeTabRIO.tab);
	}

	public boolean onItemUse(ItemStack stack, final EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
		if (!world.isRemote) {
            TileEntity tile = world.getBlockTileEntity(x, y, z);

            if (tile != null) {
                if (tile instanceof TileIO) {
                    if (((TileIO)tile).hasCoordinates()) {
                        ((TileIO)tile).getLinkedBlock().onBlockActivated(((TileIO)tile).getLinkedWorld(), ((TileIO)tile).x, ((TileIO)tile).y, ((TileIO)tile).z, player, side, fx, fy, fz);
                        return true;
                    }
                } else if (tile instanceof TileRemoteInventory) {
                    if (stack.hasTagCompound() && stack.getTagCompound().hasKey("player")) {
                        ((TileRemoteInventory)tile).owner = stack.getTagCompound().getString("player");
                        ((TileRemoteInventory)tile).lastClientState = true;
                    }
                    return true;
                }
            }
        }

        return false;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) {
			return stack;
		}
		
		if (!player.isSneaking()) {
			return stack;
		}
		
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		nbt.setString("player", player.username);
		stack.setTagCompound(nbt);
		
		ChatHelper.info(player, "chat.transceiverLink");
		
		return stack;
	}
	
	@Override
	public boolean shouldPassSneakingClickToBlock(World world, int x, int y, int z) {
		return world.getBlockId(x, y, z) == BlockHandler.blockWirelessID || world.getBlockId(x, y, z) == BlockHandler.blockIOID;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean idk) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("player")) {
			list.add("Owner: " + stack.getTagCompound().getString("player"));
		}
	}
	
	@Override
	public Icon getIconFromDamage(int damage) {
		return this.icon;
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		this.icon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "itemTransmitter");
	}

}
