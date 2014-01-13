package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.block.tile.TileRemoteInventory;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        ItemStack stack = new ItemStack(this.blockID, 1, 0);
        TileRemoteInventory logic = (TileRemoteInventory) world.getBlockTileEntity(x, y, z);

        if (logic.hasUpgrade(Upgrade.LOCK)) {
            if (logic != null) {
                if (stack.getTagCompound() == null) {
                    stack.setTagCompound(new NBTTagCompound());
                }
                
                NBTTagCompound tag = new NBTTagCompound();
                logic.writeToNBT(tag);
                stack.getTagCompound().setCompoundTag("logic", tag);
                
                if (logic.lastClientState) {
                    stack.setItemDamage(1);
                }
            }
            
            if (!player.capabilities.isCreativeMode || player.isSneaking()) {
                dropStack(world, x, y, z, stack);
            }

            return world.setBlockToAir(x, y, z);
        } else {
            return super.removeBlockByPlayer(world, player, x, y, z);
        }
    }

    protected void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int fortune) {
        
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("logic")) {
            int meta = world.getBlockMetadata(x, y, z);
            TileEntity tile = world.getBlockTileEntity(x, y, z);

            if (tile != null && tile instanceof TileIO) {
                tile.readFromNBT(stack.getTagCompound().getCompoundTag("logic"));
            }
        }
        
        onNeighborBlockChange(world, x, y, z, 0);
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

    @Override
    public void breakBlock(World world, int x, int y, int z, int id, int meta) {
        TileRemoteInventory tile = (TileRemoteInventory) world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            tile.onBlockBroken();

            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, meta));
            for (ItemStack stack : InventoryHelper.getContents(tile.upgrades)) {
                if (stack != null) this.dropBlockAsItem_do(world, x, y, z, stack);
            }
        }

        super.breakBlock(world, x, y, z, id, meta);
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
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileRemoteInventory();
	}

}
