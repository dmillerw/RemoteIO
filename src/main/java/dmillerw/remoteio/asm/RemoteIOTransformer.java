package dmillerw.remoteio.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
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
    private static final String METHOD_REDSTONE_DESC = "(L%s;III)Z";

    private boolean obfuscated = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.world.World")) {
            obfuscated = !(name.equals(transformedName));
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
            if (equals(methodNode.name, remap("isBlockIndirectlyGettingPowered", false))) {
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
            targetNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, METHOD_HANDLER, METHOD_REDSTONE, String.format(METHOD_REDSTONE_DESC, remap("net/minecraft/world/World", true)), false));
            targetNode.instructions.add(new InsnNode(Opcodes.IRETURN));

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            return bytes;
        }
    }

    private String remap(String name, boolean type) {
        if (obfuscated) {
            return type ? FMLDeobfuscatingRemapper.INSTANCE.mapType(name) : FMLDeobfuscatingRemapper.INSTANCE.map(name);
        } else {
            return name;
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
