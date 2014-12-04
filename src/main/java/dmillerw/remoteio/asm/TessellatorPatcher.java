package dmillerw.remoteio.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.PriorityQueue;

/**
 * @author dmillerw
 */
public class TessellatorPatcher implements IClassTransformer {

    public static TesselatorVertexState getVertexState(Tessellator tessellator, int[] rawBuffer, int rawBufferIndex, int vertexCount, boolean hasTexture, boolean hasBrightness, boolean hasNormals, boolean hasColor, double xOffset, double yOffset, double zOffset, float x, float y, float z) {
        if (rawBufferIndex < 1) {
            return new TesselatorVertexState(new int[0], rawBufferIndex, vertexCount, hasTexture, hasBrightness, hasNormals, hasColor);
        }

        int[] aint = new int[rawBufferIndex];
        PriorityQueue priorityqueue = new PriorityQueue(rawBufferIndex, new QuadComparator(rawBuffer, x + (float) xOffset, y + (float) yOffset, z + (float) zOffset));
        byte b0 = 32;
        int i;

        for (i = 0; i < rawBufferIndex; i += b0) {
            priorityqueue.add(Integer.valueOf(i));
        }

        for (i = 0; !priorityqueue.isEmpty(); i += b0) {
            int j = ((Integer) priorityqueue.remove()).intValue();

            for (int k = 0; k < b0; ++k) {
                aint[i + k] = rawBuffer[j + k];
            }
        }

        System.arraycopy(aint, 0, rawBuffer, 0, aint.length);
        return new TesselatorVertexState(aint, rawBufferIndex, vertexCount, hasTexture, hasBrightness, hasNormals, hasColor);
    }

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
            targetNode.instructions.clear();

            final String tessellator = remap("net/minecraft/client/renderer/Tessellator", true);
            final String vertexState = remap("net/minecraft/client/shader/TesselatorVertexState", true);

            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "rawBuffer", "[I"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "rawBufferIndex", "I"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "vertexCount", "I"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "hasTexture", "Z"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "hasBrightness", "Z"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "hasNormals", "Z"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "hasColor", "Z"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "xOffset", "D"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "yOffset", "D"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            targetNode.instructions.add(getFieldNode(tessellator, "zOffset", "D"));
            targetNode.instructions.add(new VarInsnNode(Opcodes.FLOAD, 1)); // float x
            targetNode.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2)); // float y
            targetNode.instructions.add(new VarInsnNode(Opcodes.FLOAD, 3)); // float z
            targetNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dmillerw/remoteio/asm/TessellatorPatcher", "getVertexState", "(L" + tessellator + ";[IIIZZZZDDDFFF)L" + vertexState + ";", false));
            targetNode.instructions.add(new InsnNode(Opcodes.ARETURN));

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
