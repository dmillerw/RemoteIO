package remoteio.common.core;

import remoteio.common.lib.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class TabRemoteIO extends CreativeTabs {

    public static final TabRemoteIO TAB = new TabRemoteIO();

    private TabRemoteIO() {
        super("rio");
    }

    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(ModBlocks.remoteInterface);
    }
}