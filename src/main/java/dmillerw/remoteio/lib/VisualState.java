package dmillerw.remoteio.lib;

public enum VisualState {

	INACTIVE,
	INACTIVE_BLINK,
	ACTIVE,
	ACTIVE_BLINK,
	CAMOUFLAGE_SIMPLE,
	CAMOUFLAGE_REMOTE,
	CAMOUFLAGE_BOTH;

	public boolean isCamouflage() {
		return this == CAMOUFLAGE_SIMPLE || this == CAMOUFLAGE_REMOTE || this == CAMOUFLAGE_BOTH;
	}

}