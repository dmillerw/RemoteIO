package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.RemoteIO;
import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.helper.InventoryHelper;
import com.dmillerw.remoteIO.item.ItemHandler;
import com.dmillerw.remoteIO.item.ItemUpgrade.Upgrade;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

import java.util.Random;

public abstract class BlockIOCore extends BlockContainer {

    public Icon[] icons;

    public BlockIOCore(int id) {
        super(id, Material.iron);

        this.setHardness(5F);
        this.setResistance(1F);
        this.setCreativeTab(CreativeTabRIO.tab);
    }

    public abstract TileIOCore getTile();

    public abstract int getGuiID();

    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        ItemStack stack = new ItemStack(this.blockID, 1, 0);
        TileIOCore logic = (TileIOCore) world.getBlockTileEntity(x, y, z);

        if (logic.hasUpgrade(Upgrade.LOCK, true)) {
            if (logic != null) {
                if (stack.getTagCompound() == null) {
                    stack.setTagCompound(new NBTTagCompound());
                }

                NBTTagCompound tag = new NBTTagCompound();
                logic.writeToNBT(tag);
                stack.getTagCompound().setCompoundTag("logic", tag);

                if (logic.connectionPosition() != null) {
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

            if (tile != null && tile instanceof TileIOCore) {
                tile.readFromNBT(stack.getTagCompound().getCompoundTag("logic"));
            }
        }

        onNeighborBlockChange(world, x, y, z, 0);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        if (!world.isRemote) {
            ItemStack held = player.getCurrentEquippedItem();

            if (held == null || (held.getItem() != ItemHandler.itemTool && held.getItem() != ItemHandler.itemTransmitter)) {
                player.openGui(RemoteIO.instance, getGuiID(), world, x, y, z);
                return true;
            }
        }

        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            tile.onNeighborBlockUpdate();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int id, int meta) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            tile.onBlockBreak();

            if (!tile.hasUpgrade(Upgrade.LOCK, true)) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.blockID, 1, meta));
                for (ItemStack stack : InventoryHelper.getContents(tile.upgrades)) {
                    if (stack != null) this.dropBlockAsItem_do(world, x, y, z, stack);
                }
                if (tile.camo.getStackInSlot(0) != null) this.dropBlockAsItem_do(world, x, y, z, tile.camo.getStackInSlot(0));
            }
        }

        super.breakBlock(world, x, y, z, id, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);

        ItemStack camo = tile.camo.getStackInSlot(0);
        Block block = null;
        if (camo != null && camo.itemID < 4096) {
            block = Block.blocksList[camo.itemID];
        }

        if (block != null && block.renderAsNormalBlock() && tile.hasUpgrade(Upgrade.CAMO, false)) {
            return block.getIcon(side, camo.getItemDamage());
        } else {
            if (tile != null && tile.lastClientState) {
                return getIcon(side, 1);
            } else {
                return getIcon(side, 0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public abstract Icon getIcon(int side, int meta);

    @SideOnly(Side.CLIENT)
    @Override
    public abstract void registerIcons(IconRegister register);

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        TileIOCore tile = (TileIOCore) world.getBlockTileEntity(x, y, z);

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
        return getTile();
    }

}
