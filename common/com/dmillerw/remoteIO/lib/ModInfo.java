package com.dmillerw.remoteIO.lib;

public class ModInfo {

	public static final String ID = "remoteIO";
	public static final String NAME = "Remote IO";
	public static final String VERSION = "1.0.0";
	
	public static final String BASE_PACKAGE = "com.dmillerw." + ID;
	
	public static final String COMMON_PROXY = BASE_PACKAGE + ".core.proxy.CommonProxy";
	public static final String CLIENT_PROXY = BASE_PACKAGE + ".core.proxy.ClientProxy";
	
	public static final String RESOURCE_PREFIX = ID.toLowerCase() + ":";
	
}
