package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.core.IUpdatableBlock;
import com.dmillerw.remoteIO.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSkylight extends Block implements IUpdatableBlock {

	private Icon[] icons;
	
	public BlockSkylight(int id) {
		super(id, Material.glass);
		
		setHardness(2F);
		setResistance(2F);
		setCreativeTab(CreativeTabRIO.tab);
	}
	
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote) {
			updateState(world, x, y, z);
		}
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int causeID) {
		if (!world.isRemote && causeID != this.blockID) {
			updateState(world, x, y, z);
		}
	}
	
	private void updateState(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);

		if (powered && meta == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			world.updateAllLightTypes(x, y, z);
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				Block block = Block.blocksList[world.getBlockId(x + side.offsetX, y + side.offsetY, z + side.offsetZ)];
				
				if (block != null && block instanceof IUpdatableBlock) {
					((IUpdatableBlock)block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, this.blockID, 1);
				}
			}
		} else if (!powered && meta == 1) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			world.updateAllLightTypes(x, y, z);
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				Block block = Block.blocksList[world.getBlockId(x + side.offsetX, y + side.offsetY, z + side.offsetZ)];
				
				if (block != null && block instanceof IUpdatableBlock) {
					((IUpdatableBlock)block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, this.blockID, 0);
				}
			}
		}
	}

	@Override
	public void onBlockUpdate(World world, int x, int y, int z, int causeID, int causeMeta) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean powered = causeMeta == 1;
		
		if (powered && meta == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			world.updateAllLightTypes(x, y, z);
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				Block block = Block.blocksList[world.getBlockId(x + side.offsetX, y + side.offsetY, z + side.offsetZ)];
				
				if (block != null && block instanceof IUpdatableBlock) {
					((IUpdatableBlock)block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, this.blockID, 1);
				}
			}
		} else if (!powered && meta == 1) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			world.updateAllLightTypes(x, y, z);
			for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				Block block = Block.blocksList[world.getBlockId(x + side.offsetX, y + side.offsetY, z + side.offsetZ)];
				
				if (block != null && block instanceof IUpdatableBlock) {
					((IUpdatableBlock)block).onBlockUpdate(world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, this.blockID, 0);
				}
			}
		}
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		ForgeDirection forgeSide = ForgeDirection.getOrientation(side).getOpposite();
		
		int thisMeta = world.getBlockMetadata(x, y, z);
		int thatMeta = world.getBlockMetadata(x + forgeSide.offsetX, y + forgeSide.offsetY, z + forgeSide.offsetZ);
		int thatID = world.getBlockId(x + forgeSide.offsetX, y + forgeSide.offsetY, z + forgeSide.offsetZ);
		
		return !(thisMeta == 1 && (thatMeta == 1 && thatID == this.blockID));
	}
	
	@Override
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0 ? 255 : 0;
	}
	
	@Override
	public Icon getIcon(int side, int meta) {
		return this.icons[meta];
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[2];
		
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "other/glass");
	}

}
