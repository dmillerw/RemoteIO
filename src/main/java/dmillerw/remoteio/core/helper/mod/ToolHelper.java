package dmillerw.remoteio.core.helper.mod;

import appeng.api.implementations.items.IAEWrench;
import cofh.api.item.IToolHammer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import dmillerw.remoteio.api.IIOTool;
import dmillerw.remoteio.lib.DependencyInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class ToolHelper {

    public static boolean isTool(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (itemStack == null || itemStack.getItem() == null)
            return false;

        if (itemStack.getItem() instanceof IIOTool)
            return true;

        if (ModAPIManager.INSTANCE.hasAPI(DependencyInfo.ModIds.COFH_API)) {
            if (itemStack.getItem() instanceof IToolHammer)
                return ((IToolHammer) itemStack.getItem()).isUsable(itemStack, entityPlayer, x, y, z);
        }

        if (Loader.isModLoaded(DependencyInfo.ModIds.AE2)) {
            if (itemStack.getItem() instanceof IAEWrench)
                return ((IAEWrench) itemStack.getItem()).canWrench(itemStack, entityPlayer, x, y, z);
        }

        return false;
    }
}
