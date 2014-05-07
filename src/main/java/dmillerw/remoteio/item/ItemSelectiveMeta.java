package dmillerw.remoteio.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dmillerw
 */
public class ItemSelectiveMeta extends Item {

	protected final int[] values;

	protected Map<Integer, String> names;

	public ItemSelectiveMeta(int[] values, String[] names) {
		super();

		if (values.length != names.length) {
			throw new IllegalArgumentException();
		}

		this.values = values;
		this.names = new HashMap<Integer, String>();

		for (int i=0; i<values.length; i++) {
			this.names.put(values[i], names[i]);
		}

		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i : values) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + this.names.get(stack.getItemDamage());
	}
}
