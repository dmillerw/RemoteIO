package com.dmillerw.remoteIO.block;

import com.dmillerw.remoteIO.api.documentation.IDocumentable;
import com.dmillerw.remoteIO.block.tile.TileCore;
import com.dmillerw.remoteIO.block.tile.TileHeater;
import com.dmillerw.remoteIO.block.tile.TileReservoir;
import com.dmillerw.remoteIO.lib.ModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockMachine extends BlockCore implements IDocumentable {

	public static final String[] INTERNAL_NAMES = new String[] {"heater", "reservoir"};
	public static final String[] NAMES = new String[] {"Lava-Powered Heater", "Water Reservoir"};
	
	public Icon[] icons;
	
	public BlockMachine(int id) {
		super(id);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		for (int i=0; i<INTERNAL_NAMES.length; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		
		if (meta == 0) {
			TileHeater tile = (TileHeater) world.getBlockTileEntity(x, y, z);
			return tile.hasLava ? icons[0] : icons[2];
		} else if (meta == 1) {
			TileReservoir tile = (TileReservoir) world.getBlockTileEntity(x, y, z);
			return tile.hasWater ? icons[1] : icons[2];
		}
		
		return icons[2];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		return icons[meta];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.icons = new Icon[3];
		
		this.icons[2] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "machine/blank");
		this.icons[1] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "machine/reservoir");
		this.icons[0] = register.registerIcon(ModInfo.RESOURCE_PREFIX + "machine/heater");
	}
	
	@Override
	public TileCore getTile(int meta) {
		switch(meta) {
		case 0: return new TileHeater();
		case 1: return new TileReservoir();
		default: return null;
		}
	}

    @Override
    public String getKey(ItemStack stack) {
        return stack.getItemDamage() == 0 ? "MACHINE_HEATER" : "MACHINE_RESERVOIR";
    }
}