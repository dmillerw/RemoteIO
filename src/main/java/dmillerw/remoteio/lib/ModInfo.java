package dmillerw.remoteio.lib;

public class ModInfo {

	public static final String ID = "RIO";
	public static final String NAME = "RemoteIO";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "required-after:Forge@[10.12.1.1121,)";

	public static final String CLIENT = "dmillerw.remoteio.core.proxy.ClientProxy";
	public static final String SERVER = "dmillerw.remoteio.core.proxy.CommonProxy";

	public static final Object RESOURCE_PREFIX = "remoteio:";
}
