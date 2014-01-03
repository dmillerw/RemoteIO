package com.dmillerw.remoteIO.core.helper;

import com.dmillerw.remoteIO.lib.ModInfo;
import com.google.common.base.Charsets;
import cpw.mods.fml.common.registry.LanguageRegistry;

import java.io.*;
import java.util.Properties;

public class LocalizationHelper {

private static final String LANG_DIR = "/assets/" + ModInfo.ID.toLowerCase() + "/lang";
	
	public static final String[] LANGS = new String[] {"en_US", "de_DE"};
	
	public static void initializeLocalization() {
		for (String lang : LANGS) {
			LanguageRegistry.instance().loadLocalization(LANG_DIR + "/" + lang + ".properties", lang, false);
		}
	}

	public static void initializeUserLocalization(File path) {
		if (!path.exists()) {
			path.mkdirs();
		}
		
		for (File file : path.listFiles()) {
			try {
				if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equalsIgnoreCase("properties")) {
					InputStream is = new FileInputStream(file);
					loadLocalization(is, file.getName().substring(0, file.getName().lastIndexOf(".")), false);
				}
			} catch(Exception ex) {
				IOLogger.warn("Failed to load user localization file! [" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "]");
			}
		}
	}

	private static void loadLocalization(InputStream is, String lang, boolean isXML) {
		Properties langPack = new Properties();

		try {
			if (isXML) {
				langPack.loadFromXML(is);
			} else {
				langPack.load(new InputStreamReader(is, Charsets.UTF_8));
			}

			LanguageRegistry.instance().addStringLocalization(langPack, lang);
		} catch (IOException e) {
			IOLogger.warn("Failed to load user localization file! [" + lang + "]");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ex) {
				// HUSH
			}
		}
	}
	
}
