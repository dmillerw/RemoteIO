package dmillerw.remoteio.item;

import dmillerw.remoteio.core.TabRemoteIO;
import dmillerw.remoteio.core.UpgradeType;

/**
 * @author dmillerw
 */
public class ItemUpgradeChip extends ItemSelectiveMeta {

	public ItemUpgradeChip() {
		super(new int[] {
			UpgradeType.REMOTE_CAMO,
			UpgradeType.REMOTE_ACCESS
		},

		new String[] {
			"remote_camo",
			"remote_access"
		});

		setCreativeTab(TabRemoteIO.TAB);
	}

}
