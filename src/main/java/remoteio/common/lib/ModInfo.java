package remoteio.common.lib;

public class ModInfo {

    public static final String ID = "RIO";
    public static final String NAME = "RemoteIO";
    public static final String VERSION = "%MOD_VERSION%";
    public static final String DEPENDENCIES = "required-after:Forge@[%FORGE_VERSION%,);after:Waila";

    public static final String CLIENT = "remoteio.common.core.proxy.ClientProxy";
    public static final String SERVER = "remoteio.common.core.proxy.CommonProxy";

    public static final Object RESOURCE_PREFIX = "remoteio:";
}
