package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.api.documentation.IDocumentable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.dmillerw.remoteIO.core.CreativeTabRIO;
import com.dmillerw.remoteIO.lib.ModInfo;

public class BlockSkylight extends Block implements IUpdatableBlock, IDocumentable {

	private Icon blank;
	
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
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}
	
	@Override
	public boolean isOpaqueCube() {
        return false;
    }
	
	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 0 ? 255 : 0;
	}
	
	public boolean shouldConnectToBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, int par6) {
        return par5 == this.blockID;
    }

	@Override
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 0 ? blank : getConnectedBlockTexture(par1IBlockAccess, par2, par3, par4, par5, icons);
	}

    public Icon getConnectedBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, Icon[] icons)
    {

        boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

        switch (par5)
        {
        case 0:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[11];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[5];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[6];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenDown)
            {
                return icons[3];
            }
            else if (isOpenUp)
            {
                return icons[4];
            }
            else if (isOpenLeft)
            {
                return icons[2];
            }
            else if (isOpenRight)
            {
                return icons[1];
            }
            break;
        case 1:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[11];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[5];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[6];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenDown)
            {
                return icons[3];
            }
            else if (isOpenUp)
            {
                return icons[4];
            }
            else if (isOpenLeft)
            {
                return icons[2];
            }
            else if (isOpenRight)
            {
                return icons[1];
            }
            break;
        case 2:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[9];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[8];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[4];
            }
            else if (isOpenRight)
            {
                return icons[3];
            }
            break;
        case 3:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[10];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[7];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[3];
            }
            else if (isOpenRight)
            {
                return icons[4];
            }
            break;
        case 4:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[10];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[9];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[8];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[7];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[3];
            }
            else if (isOpenRight)
            {
                return icons[4];
            }
            break;
        case 5:
            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
            {
                isOpenDown = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
            {
                isOpenUp = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
            {
                isOpenLeft = true;
            }

            if (shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlockId(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
            {
                isOpenRight = true;
            }

            if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[15];
            }
            else if (isOpenUp && isOpenDown && isOpenLeft)
            {
                return icons[13];
            }
            else if (isOpenUp && isOpenDown && isOpenRight)
            {
                return icons[14];
            }
            else if (isOpenUp && isOpenLeft && isOpenRight)
            {
                return icons[11];
            }
            else if (isOpenDown && isOpenLeft && isOpenRight)
            {
                return icons[12];
            }
            else if (isOpenDown && isOpenUp)
            {
                return icons[6];
            }
            else if (isOpenLeft && isOpenRight)
            {
                return icons[5];
            }
            else if (isOpenDown && isOpenLeft)
            {
                return icons[9];
            }
            else if (isOpenDown && isOpenRight)
            {
                return icons[10];
            }
            else if (isOpenUp && isOpenLeft)
            {
                return icons[7];
            }
            else if (isOpenUp && isOpenRight)
            {
                return icons[8];
            }
            else if (isOpenDown)
            {
                return icons[1];
            }
            else if (isOpenUp)
            {
                return icons[2];
            }
            else if (isOpenLeft)
            {
                return icons[4];
            }
            else if (isOpenRight)
            {
                return icons[3];
            }
            break;
        }

        return icons[0];
    }

	@Override
	public Icon getIcon(int side, int meta) {
		return icons[0];
	}
	
	@Override
	public void registerIcons(IconRegister register) {
		this.blank = register.registerIcon(ModInfo.RESOURCE_PREFIX + "blank");

		this.icons = new Icon[16];
		icons[0]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass");
        icons[1]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_1_d");
        icons[2]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_1_u");
        icons[3]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_1_l");
        icons[4]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_1_r");
        icons[5]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_h");
        icons[6]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_v");
        icons[7]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_dl");
        icons[8]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_dr");
        icons[9]  = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_ul");
        icons[10] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_2_ur");
        icons[11] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_3_d");
        icons[12] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_3_u");
        icons[13] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_3_l");
        icons[14] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_3_r");
        icons[15] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "skylight/glass_4");
	}

    @Override
    public String getKey(ItemStack stack) {
        return "SKYLIGHT";
    }
}
