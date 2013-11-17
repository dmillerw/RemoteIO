package com.dmillerw.remoteIO.core.helper;

import java.util.logging.Logger;

import com.dmillerw.remoteIO.lib.ModInfo;

import cpw.mods.fml.common.FMLLog;

public class IOLogger {

	public static Logger logger;
	
	static {
		logger = Logger.getLogger(ModInfo.ID);
		logger.setParent(FMLLog.getLogger());
	}
	
	public static void info(String msg) {
		logger.info(msg);
	}
	
	public static void warn(String msg) {
		logger.warning(msg);
	}
	
}
