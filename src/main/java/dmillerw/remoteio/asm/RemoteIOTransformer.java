package dmillerw.remoteio.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author dmillerw
 */
public class RemoteIOTransformer implements IClassTransformer {

    private static final String METHOD_HANDLER = "dmillerw/remoteio/core/tracker/RedstoneTracker";

    private static final String METHOD_REDSTONE = "isBlockIndirectlyGettingPowered";
    private static final String METHOD_REDSTONE_DESC = "(Lnet/minecraft/world/World;III)Z";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.world.World")) {
            return transformWorld(basicClass);
        } else {
            return basicClass;
        }
    }

    private byte[] transformWorld(byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode targetNode = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, "isBlockIndirectlyGettingPowered")) {
                targetNode = methodNode;
                break;
            }
        }

        if (targetNode != null) {
            targetNode.instructions.clear();
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 2));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
            targetNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, METHOD_HANDLER, METHOD_REDSTONE, METHOD_REDSTONE_DESC, false));
            targetNode.instructions.add(new InsnNode(Opcodes.IRETURN));

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            return bytes;
        }
    }

    private boolean equals(String string, String ... compare) {
        for (String s : compare) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
