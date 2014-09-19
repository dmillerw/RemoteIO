package dmillerw.remoteio.core.helper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author dmillerw
 */
public class OreHelper {

    public static String getOreTag(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        if (ids != null && ids.length > 0) {
            String tag = OreDictionary.getOreName(ids[0]);
            if (!tag.isEmpty() && !(tag.equalsIgnoreCase("unknown"))) {
                return tag;
            }
        }
        return "";
    }
}
