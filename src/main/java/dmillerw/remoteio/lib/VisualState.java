package dmillerw.remoteio.lib;

public enum VisualState {

    INACTIVE,
    INACTIVE_BLINK,
    ACTIVE,
    ACTIVE_BLINK,
    CAMOUFLAGE_REMOTE;

    public boolean isCamouflage() {
        return this == CAMOUFLAGE_REMOTE;
    }
}