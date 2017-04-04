package me.dmillerw.remoteio.lib;

import me.dmillerw.remoteio.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by dmillerw
 */
public class ModTab extends CreativeTabs {

    public static final CreativeTabs TAB = new ModTab();

    public ModTab() {
        super(ModInfo.MOD_ID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModBlocks.remote_interface_item,1);
    }
}
