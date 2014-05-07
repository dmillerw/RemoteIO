package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.TransferType;

/**
 * @author dmillerw
 */
public class ItemTransferChip extends ItemSelectiveMeta {

	public ItemTransferChip() {
		super(new int[] {
			TransferType.MATTER_ITEM,
			TransferType.MATTER_FLUID
		},

		new String[] {
			"item",
			"fluid"
		});

		setCreativeTab(TabRemoteIO.TAB);
	}

}
