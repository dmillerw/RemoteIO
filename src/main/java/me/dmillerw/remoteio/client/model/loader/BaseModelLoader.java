package me.dmillerw.remoteio.client.model.loader;

import com.google.common.collect.Maps;
import me.dmillerw.remoteio.client.model.model.MagicalModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.Map;
import java.util.function.Predicate;

/**
 * @author dmillerw
 */
public class BaseModelLoader implements ICustomModelLoader {

    private static Map<Predicate<String>, IModel> modelRegistry = Maps.newHashMap();
    static {
        modelRegistry.put((new Predicate<String>() {
            @Override
            public boolean test(String path) {
                return path.contains("block") && path.contains("remote_interface");
            }
        }), new MagicalModel());
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals("remoteio"))
            return false;

        final String path = modelLocation.getResourcePath();

        for (Map.Entry<Predicate<String>, IModel> entry : modelRegistry.entrySet()) {
            if (entry.getKey().test(path))
                return true;
        }

        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        final String path = modelLocation.getResourcePath();

        for (Map.Entry<Predicate<String>, IModel> entry : modelRegistry.entrySet()) {
            if (entry.getKey().test(path))
                return entry.getValue();
        }

        return null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}