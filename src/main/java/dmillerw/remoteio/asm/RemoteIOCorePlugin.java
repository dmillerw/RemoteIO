package dmillerw.remoteio.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @author dmillerw
 */
public class RemoteIOCorePlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"dmillerw.remoteio.asm.RemoteIOTransformer", "dmillerw.remoteio.asm.TessellatorPatcher"};
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

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
