package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.lib.ModInfo;

/**
 * @author dmillerw
 */
public class ItemBlankPlate
extends Item{
    public ItemBlankPlate() {
        super();

        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "plate_blank");
    }
}
