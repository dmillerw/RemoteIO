package me.dmillerw.remoteio.lib;

import me.dmillerw.remoteio.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by dmillerw
 */
public class ModTab extends CreativeTabs {

    public static final CreativeTabs TAB = new ModTab();

    public ModTab() {
        super(ModInfo.MOD_ID);
    }

    @Override
    public Item getTabIconItem() {
        return ModBlocks.remote_interface_item;
    }
}
