package dmillerw.remoteio.core.handler;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final String LANG_DIR = "https://api.github.com/repos/%s/%s/contents/%s?ref=%s";
    private static final String RAW_URL = "https://raw.githubusercontent.com/%s/%s/%s";

    private static final String[] INSTANCE = new String[] {"instance", "field_74817_a", "c"};
    private static final String[] LANGUAGE_MAP = new String[] {"languageList", "field_74816_c", "d"};
    private static final String[] LAST_UPDATE = new String[] {"lastUpdateTimeInMilliseconds", "field_150511_e", "e"};

    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, Map<String, String>> loadedLangFiles = Maps.newConcurrentMap();

    private final String langUrl;
    private final String rawUrl;

    private boolean optout = false;

    public LocalizationUpdater(String owner, String repo, String branch, String langPath) {
        this(String.format(LANG_DIR, owner, repo, langPath, branch), String.format(RAW_URL, owner, repo, branch) + "/%s");
    }

    public LocalizationUpdater(String langUrl, String rawUrl) {
        this.langUrl = langUrl;
        this.rawUrl = rawUrl;

        System.out.println(langUrl);
        System.out.println(rawUrl);
    }

    // Called in preInit to start the download thread
    public void initializeThread(Configuration configuration) {
        optout = configuration.get("optout", "localization_update", false, "Opt-out of localization updates, and only use lang files packaged with the JAR").getBoolean(false);
        if (!optout) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(langUrl);
                        InputStream con = url.openStream();
                        String data = new String(ByteStreams.toByteArray(con));
                        con.close();

                        Map<String, Object>[] json = new Gson().fromJson(data, Map[].class);

                        for (Map<String, Object> aJson : json) {
                            String name = ((String) aJson.get("name"));
                            if (name.endsWith(".lang")) {
                                LOGGER.info("Discovered " + name + ". Downloading...");
                                URL url1 = new URL(String.format(rawUrl, aJson.get("path")));
                                InputStream con1 = url1.openStream();
                                Map<String, String> map = StringTranslate.parseLangFile(con1);
                                LocalizationUpdater.this.loadedLangFiles.put(name.substring(0, name.lastIndexOf(".lang")), map);
                                con1.close();
                            }
                        }
                    } catch (Exception ex) {
                        // Most likely due to the lack of an internet connection. No need to print
                        if (ex instanceof UnknownHostException)
                            optout = true;
                        else
                            LOGGER.warn("Failed to update localization!", ex);
                    }
                }
            });
            thread.start();
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerListener() {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(IResourceManager resourceManager) {
                LocalizationUpdater.this.loadLangFiles();
            }
        });
    }

    // Called whenever the resource manager reloads, to load any lang files that the thread gathered
    private void loadLangFiles() {
        if (!optout) {
            HashMap<String, String> map = (HashMap<String, String>) loadedLangFiles.get(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode());
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
                    for (String name : INSTANCE) {
                        Field field = StringTranslate.class.getDeclaredField(name);
                        if (field != null) {
                            field.setAccessible(true);
                            instance = (StringTranslate) field.get(StringTranslate.class);
                            field.setAccessible(false);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return instance;
        }

        public static void inject(HashMap<String, String> map) {
            try {
                Map<String, String> languageMap = ObfuscationReflectionHelper.getPrivateValue(StringTranslate.class, getInstance(), LANGUAGE_MAP);
                languageMap.putAll(map);
                ObfuscationReflectionHelper.setPrivateValue(StringTranslate.class, getInstance(), System.currentTimeMillis(), LAST_UPDATE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
