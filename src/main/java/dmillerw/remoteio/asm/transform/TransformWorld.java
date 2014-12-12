package dmillerw.remoteio.asm.transform;

import dmillerw.remoteio.asm.MappingHelper;
import dmillerw.remoteio.asm.mapping.MappingConstants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author dmillerw
 */
public class TransformWorld implements ITransformer {

    private static final String METHOD_HANDLER = "dmillerw/remoteio/core/tracker/RedstoneTracker";

    private static final String METHOD_REDSTONE = "getIndirectPowerLevelTo";
    private static final String METHOD_REDSTONE_DESC = "(L%s;IIII)I";

    @Override
    public String[] getClasses() {
        return new String[] {"net.minecraft.world.World"};
    }

    @Override
    public byte[] transform(String name, byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode targetNode = null;

        final String world = MappingConstants.Type.get(MappingConstants.Type.WORLD);

        for (MethodNode methodNode : classNode.methods) {
            if (MappingConstants.Method.equals(methodNode, MappingConstants.Method.GET_INDIRECT_POWER_LEVEL_TO, MappingConstants.Method.Desc.GET_INDIRECT_POWER_LEVEL_TO)) {
                targetNode = methodNode;
                MappingHelper.logger.info("Found method 'getIndirectPowerLevelTo'");
                break;
            }
        }

        if (targetNode != null) {
            targetNode.instructions.clear();
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
            targetNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, METHOD_HANDLER, METHOD_REDSTONE, String.format(METHOD_REDSTONE_DESC, world), false));
            targetNode.instructions.add(new InsnNode(Opcodes.IRETURN));

            MappingHelper.logger.warn("Successfully transformed World.class!");
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            MappingHelper.logger.warn("Failed to transform World.class");
            return basicClass;
        }
    }
}
