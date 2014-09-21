package dmillerw.remoteio.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemBlockMulti extends ItemBlock {

    private final String[] names;

    public ItemBlockMulti(Block block, String[] names) {
        super(block);
        this.names = names;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + names[stack.getItemDamage() % names.length];
    }
}
