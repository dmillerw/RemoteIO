package me.dmillerw.remoteio.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Created by dmillerw
 */
@IFMLLoadingPlugin.SortingIndex(1001)
public class CoreLoader implements IFMLLoadingPlugin {

    public static Logger logger = LogManager.getLogger("RemoteIO:ASM");
    public static Boolean obfuscated = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"me.dmillerw.remoteio.asm.transformer.CoreTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        CoreLoader.obfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
