package dmillerw.remoteio.core.handler;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.config.Configuration;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dmillerw
 */
public class LocalizationUpdater {

    private static final String LANG_DIR = "https://api.github.com/repos/dmillerw/RemoteIO/contents/src/main/resources/assets/remoteio/lang?ref=17";
    private static final String RAW_URL = "https://raw.githubusercontent.com/dmillerw/RemoteIO/17/%s";

    public static Map<String, Map<String, String>> loadedLangFiles = Maps.newConcurrentMap();

    private static boolean optout = false;

    // Called in preInit to start the download thread
    public static void initializeThread(Configuration configuration) {
        optout = configuration.get("optout", "localization_update", false, "Opt-out of localization updates, and only use lang files packaged with the JAR").getBoolean(false);
        if (!optout) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(LANG_DIR);
                        InputStream con = url.openStream();
                        String data = new String(ByteStreams.toByteArray(con));
                        con.close();

                        Map<String, Object>[] json = new Gson().fromJson(data, Map[].class);

                        for (Map<String, Object> aJson : json) {
                            String name = ((String) aJson.get("name"));
                            if (name.endsWith(".lang")) {
                                System.out.println("Discovered " + name + ". Downloading...");
                                URL url1 = new URL(String.format(RAW_URL, aJson.get("path")));
                                InputStream con1 = url1.openStream();
                                Map<String, String> map = StringTranslate.parseLangFile(con1);
                                LocalizationUpdater.loadedLangFiles.put(name.substring(0, name.lastIndexOf(".lang")), map);
                                con1.close();
                            }
                        }
                    } catch (Exception ex) {
                        // Most likely due to the lack of an internet connection. No need to print
                        if (ex instanceof UnknownHostException)
                            optout = true;
                        else
                            ex.printStackTrace();
                    }
                }
            });
            thread.start();

            ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
                @Override
                public void onResourceManagerReload(IResourceManager resourceManager) {
                    LocalizationUpdater.loadLangFiles();
                }
            });
        }
    }

    // Called in postInit to load any lang files that the thread gathered
    public static void loadLangFiles() {
        if (!optout) {
            HashMap<String, String> map = (HashMap<String, String>) LocalizationUpdater.loadedLangFiles.get(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode());
            if (map != null) {
                StringTranslateDelegate.inject(map);
            }
        }
    }

    private static class StringTranslateDelegate {
        private static StringTranslate instance;

        public static StringTranslate getInstance() {
            if (instance == null) {
                try {
                    Field field = StringTranslate.class.getDeclaredField("instance");
                    if (field != null) {
                        field.setAccessible(true);
                        instance = (StringTranslate) field.get(StringTranslate.class);
                        field.setAccessible(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return instance;
        }

        public static void inject(HashMap<String, String> map) {
            try {
                Field languageMap;
                Field lastUpdate;

                languageMap = StringTranslate.class.getDeclaredField("languageList");
                lastUpdate = StringTranslate.class.getDeclaredField("lastUpdateTimeInMilliseconds");
                languageMap.setAccessible(true);
                lastUpdate.setAccessible(true);

                ((Map<String, String>)languageMap.get(getInstance())).putAll(map);
                lastUpdate.set(getInstance(), System.currentTimeMillis());

                languageMap.setAccessible(false);
                lastUpdate.setAccessible(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
