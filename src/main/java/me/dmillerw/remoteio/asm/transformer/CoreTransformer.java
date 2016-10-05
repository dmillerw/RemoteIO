package me.dmillerw.remoteio.asm.transformer;

import me.dmillerw.remoteio.asm.CoreLoader;
import net.minecraft.launchwrapper.IClassTransformer;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @author dmillerw
 */
public class CoreTransformer implements IClassTransformer {

    public List<ITransformer> transformerList = Lists.newArrayList();

    private void addTransformer(ITransformer transformer) {
        transformerList.add(transformer);
        CoreLoader.logger.info("Adding transformer for: " + Arrays.toString(transformer.getClasses()));
    }

    public CoreTransformer() {
        addTransformer(new TransformEntityPlayer());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (ITransformer transformer : transformerList) {
            for (String string : transformer.getClasses()) {
                if (transformedName.equals(string)) {
                    CoreLoader.logger.info("Beginning to transform '" + transformedName + "'");
                    return transformer.transform(transformedName, basicClass);
                }
            }
        }
        return basicClass;
    }
}