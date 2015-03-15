package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.lib.ModInfo;

//TODO: Migrate Location Chip Logic to this
public final class ItemLinker
extends Item{
    public ItemLinker(){
        this.setMaxStackSize(1);
        this.setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void registerIcons(IIconRegister register){
        this.itemIcon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "linker");
    }
}