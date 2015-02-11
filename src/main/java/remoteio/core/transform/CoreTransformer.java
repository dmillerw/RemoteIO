package remoteio.core.transform;

import net.minecraft.launchwrapper.IClassTransformer;
import remoteio.core.MappingHelper;

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
        MappingHelper.logger.info("Adding transformer for: " + Arrays.toString(transformer.getClasses()));
    }

    public CoreTransformer() {
        // addTransformer(new TransformWorld());
        addTransformer(new TransformTessellator());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (ITransformer transformer : transformerList) {
            for (String string : transformer.getClasses()) {
                if (transformedName.equals(string)) {
                    MappingHelper.logger.info("Beginning to transform '" + transformedName + "'");
                    return transformer.transform(transformedName, basicClass);
                }
            }
        }
        return basicClass;
    }
}
