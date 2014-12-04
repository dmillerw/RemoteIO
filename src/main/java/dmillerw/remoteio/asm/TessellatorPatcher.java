package dmillerw.remoteio.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author dmillerw
 */
public class TessellatorPatcher implements IClassTransformer {

    private boolean obfuscated = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.renderer.Tessellator")) {
            obfuscated = !name.equals(transformedName);
            return transformTessellator(basicClass);
        } else {
            return basicClass;
        }
    }

    private byte[] transformTessellator(byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode targetNode = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, remap("getVertexState", false))) {
                targetNode = methodNode;
                break;
            }
        }

        if (targetNode != null) {
            final String tessellator = remap("net/minecraft/client/renderer/Tessellator", true);
            final String vertexState = remap("net/minecraft/client/shader/TesselatorVertexState", true);

            InsnList insnList = new InsnList();

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Add this (tessellator instance) to stack
            insnList.add(getFieldNode(tessellator, "rawBufferIndex", "I")); // add rawBufferIndex variable
            insnList.add(new InsnNode(Opcodes.ICONST_1)); // add number 1 to stack
            LabelNode l1 = new LabelNode(new Label());
            insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, l1));
            insnList.add(new TypeInsnNode(Opcodes.NEW, vertexState)); // create new vertex state object
            insnList.add(new InsnNode(Opcodes.DUP)); // duplicate top object on stack
            insnList.add(new InsnNode(Opcodes.ICONST_0)); // add number 0 to stack
            insnList.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT)); // create new array of type int with size 0

            // Add important variables to stack
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "rawBufferIndex", "I"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "vertexCount", "I"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "hasTexture", "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "hasBrightness", "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "hasNormals", "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, "hasColor", "Z"));

            // Call init method on TesselatorVertexState object
            insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, vertexState, "<init>", "([IIIZZZZ)V", false));

            insnList.add(new InsnNode(Opcodes.ARETURN)); // return
            insnList.add(l1); // jump back
            insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null)); // clear frame (?)

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            return bytes;
        }
    }

    private FieldInsnNode getFieldNode(String owner, String field, String desc) {
        return new FieldInsnNode(Opcodes.GETFIELD, owner, remap(owner, field, desc), desc);
    }

    private String remap(String name, boolean type) {
        if (obfuscated) {
            return type ? FMLDeobfuscatingRemapper.INSTANCE.mapType(name) : FMLDeobfuscatingRemapper.INSTANCE.map(name);
        } else {
            return name;
        }
    }

    private String remap(String owner, String name, String desc) {
        if (obfuscated)
            owner = remap(owner, true);

        if (obfuscated)
            return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, name, desc);
        else
            return name;
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
