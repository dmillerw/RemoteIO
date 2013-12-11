package com.dmillerw.remoteIO.block;

import java.util.Random;

import net.minecraft.block.Block;
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

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileIO;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.core.tracker.BlockTracker;
import com.dmillerw.remoteIO.item.ItemTool;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
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
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        ItemStack stack = new ItemStack(this.blockID, 1, 0);
        TileIO logic = (TileIO) world.getBlockTileEntity(x, y, z);
        if (logic.validCoordinates) {
        	BlockTracker.getInstance().stopTracking(world.provider.dimensionId, logic.x, logic.y, logic.z);
        }

        if (logic.hasUpgrade(Upgrade.LOCK)) {
        	if (logic != null) {
            	if (stack.getTagCompound() == null) {
            		stack.setTagCompound(new NBTTagCompound());
            	}
            	
                NBTTagCompound tag = new NBTTagCompound();
                logic.writeToNBT(tag);
                stack.getTagCompound().setCompoundTag("logic", tag);
                
                if (logic.validCoordinates) {
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
			if (!player.isSneaking() && (player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemTool))) {
				player.openGui(RemoteIO.instance, 0, world, x, y, z);
				return true;
			}
		}
		
		return false;
	}
	
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.setRedstoneState(world.isBlockIndirectlyGettingPowered(x, y, z));
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);

		if (tile != null) {
			tile.onBlockBroken();

			if (!tile.hasUpgrade(Upgrade.LOCK)) {
				this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, meta));
				for (ItemStack stack : InventoryHelper.getContents(tile.upgrades)) {
					if (stack != null) this.dropBlockAsItem_do(world, x, y, z, stack);
				}
			}
		}
		
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);

		ItemStack camo = tile.camo.getStackInSlot(0);
		Block block = null;
		if (camo != null && camo.itemID < 4096) {
			block = Block.blocksList[camo.itemID];
		}
		
		if (block != null && block.renderAsNormalBlock() && tile.hasUpgrade(Upgrade.CAMO)) {
			return block.getIcon(side, camo.getItemDamage());
		} else {
			if (tile != null && tile.validCoordinates) {
				return this.icons[1];
			} else {
				return this.icons[0];
			}
		}
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
		
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIO");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blockIOInactive");
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileIO tile = (TileIO) world.getBlockTileEntity(x, y, z);
        
        if (tile != null && tile.redstoneState) {
        	double d0 = 0.0625D;
    		for (int l = 0; l < 6; ++l) {
    			double d1 = (double) ((float) x + random.nextFloat());
    			double d2 = (double) ((float) y + random.nextFloat());
    			double d3 = (double) ((float) z + random.nextFloat());

    			if (l == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
    				d2 = (double) (y + 1) + d0;
    			}

    			if (l == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
    				d2 = (double) (y + 0) - d0;
    			}

    			if (l == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
    				d3 = (double) (z + 1) + d0;
    			}

    			if (l == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
    				d3 = (double) (z + 0) - d0;
    			}

    			if (l == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
    				d1 = (double) (x + 1) + d0;
    			}

    			if (l == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
    				d1 = (double) (x + 0) - d0;
    			}

    			if (d1 < (double) x || d1 > (double) (x + 1) || d2 < 0.0D || d2 > (double) (y + 1) || d3 < (double) z || d3 > (double) (z + 1)) {
    				world.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
    			}
    		}
        }
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileIO();
	}
	
}
