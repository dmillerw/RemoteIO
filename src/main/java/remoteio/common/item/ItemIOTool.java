package remoteio.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import remoteio.api.IIOTool;
import remoteio.common.core.TabRemoteIO;
import remoteio.common.lib.ModInfo;

/**
 * @author dmillerw
 */
public class ItemIOTool
extends Item implements IIOTool {
    public ItemIOTool() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(TabRemoteIO.TAB);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(ModInfo.RESOURCE_PREFIX + "tool");
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }
}
