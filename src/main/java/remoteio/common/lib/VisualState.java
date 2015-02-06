package remoteio.common.lib;

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

    @Override
    public String toString(){
        switch(this)
        {
            case INACTIVE_BLINK:
            case INACTIVE:{
                return "Inactive";
            }
            case ACTIVE:
            case ACTIVE_BLINK:{
                return "Active";
            }
            default:{
                return null;
            }
        }
    }
}