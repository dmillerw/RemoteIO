package dmillerw.remoteio.lib;

public enum VisualState {

    INACTIVE,
    INACTIVE_BLINK,
    ACTIVE,
    ACTIVE_BLINK,
    CAMOUFLAGE_REMOTE,
    CAMOUFLAGE_SIMPLE,
    CAMOUFLAGE_BOTH;

    public boolean isCamouflage() {
        return this == CAMOUFLAGE_REMOTE || this == CAMOUFLAGE_SIMPLE || this == CAMOUFLAGE_BOTH;
    }
}