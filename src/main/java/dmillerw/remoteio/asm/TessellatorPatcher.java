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

    private static boolean capture = false;

    public static double rotationAngle = 0D;

    public static double offsetX = 0D;
    public static double offsetZ = 0D;

    public static void startCapturing() {
        reset();
        capture = true;
    }

    public static void reset() {
        capture = false;
        rotationAngle = 0;
        offsetX = 0;
        offsetZ = 0;
    }

    public static double[] rotatePoint(double x, double y, double z) {
        if (capture) {
            final double radians = Math.toRadians(rotationAngle);
            final double sin = Math.sin(radians);
            final double cos = Math.cos(radians);

            double nx = x * cos - z * sin;
            double nz = x * sin + z * cos;

            x = nx;
            z = nz;
        }

        return new double[] {x, y, z};
    }

    public static double[] rotatePointWithOffset(double x, double y, double z) {
        return rotatePointWithOffset(x, y, z, offsetX, 0, offsetZ);
    }

    public static double[] rotatePointWithOffset(double x, double y, double z, double offsetX, double offsetY, double offsetZ) {
        if (capture) {
            final double radians = Math.toRadians(rotationAngle);
            final double sin = Math.sin(radians);
            final double cos = Math.cos(radians);

            x += offsetX;
            z += offsetZ;

            x += 0.5;
            z += 0.5;

            double nx = x * cos - z * sin;
            double nz = x * sin + z * cos;

            x = nx;
            z = nz;

            x -= offsetX;
            z -= offsetZ;

            x -= 0.5;
            z -= 0.5;
        }

        return new double[] {x, y, z};
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

        MethodNode getVertexStateNode = null;
        MethodNode addVertexNode = null;
        MethodNode setNormalNode = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, remap("getVertexState", false))) {
                getVertexStateNode = methodNode;
            } else if (equals(methodNode.name, remap("addVertex", false))) {
                addVertexNode = methodNode;
            } else if (equals(methodNode.name, remap("setNormal", false))) {
                setNormalNode = methodNode;
            }
        }

        final String tessellator = remap("net/minecraft/client/renderer/Tessellator", true);

        if (getVertexStateNode != null) {
            final String vertexState = remap("net/minecraft/client/shader/TesselatorVertexState", true);

            InsnList insnList = new InsnList();

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Add this (tessellator instance) to stack
            insnList.add(getFieldNode(tessellator, "rawBufferIndex", "I")); // add rawBufferIndex variable
            insnList.add(new InsnNode(Opcodes.ICONST_1)); // add number 1 to stack
            LabelNode l1 = new LabelNode(new Label());
            insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, l1)); // if rawBufferIndex is less than or equal to 1
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

            getVertexStateNode.instructions.insertBefore(getVertexStateNode.instructions.get(0), insnList);
        }

        if (addVertexNode != null) {
            InsnList insnList = new InsnList();

            // Load the three parameters
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 1));
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 3));
            insnList.add(new VarInsnNode(Opcodes.DLOAD, 5));

            // Invoke rotation method
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dmillerw/remoteio/asm/TessellatorPatcher", "rotatePointWithOffset", "(DDD)[D", false));

            insnList.add(new VarInsnNode(Opcodes.ASTORE, 6)); // Store returned array

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 6)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_0)); // Load 0 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new VarInsnNode(Opcodes.DSTORE, 1)); // Save value to param 1

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 6)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_1)); // Load 1 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new VarInsnNode(Opcodes.DSTORE, 3)); // Save value to param 3

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 6)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_2)); // Load 2 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new VarInsnNode(Opcodes.DSTORE, 5)); // Save value to param 5

            addVertexNode.instructions.insertBefore(addVertexNode.instructions.get(0), insnList);
        }

        if (setNormalNode != null) {
            InsnList insnList = new InsnList();

            // Load and convert the parameters
            insnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
            insnList.add(new InsnNode(Opcodes.F2D));
            insnList.add(new VarInsnNode(Opcodes.FLOAD, 2));
            insnList.add(new InsnNode(Opcodes.F2D));
            insnList.add(new VarInsnNode(Opcodes.FLOAD, 3));
            insnList.add(new InsnNode(Opcodes.F2D));

            // Invoke rotation method
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "dmillerw/remoteio/asm/TessellatorPatcher", "rotatePoint", "(DDD)[D", false));

            insnList.add(new VarInsnNode(Opcodes.ASTORE, 3)); // Store the result

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_0)); // Load 0 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new InsnNode(Opcodes.D2F)); // Cast
            insnList.add(new VarInsnNode(Opcodes.FSTORE, 1)); // Save value to param 1

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_1)); // Load 1 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new InsnNode(Opcodes.D2F)); // Cast
            insnList.add(new VarInsnNode(Opcodes.FSTORE, 2)); // Save value to param 2

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // Add array to stack
            insnList.add(new InsnNode(Opcodes.ICONST_2)); // Load 2 on to stack
            insnList.add(new InsnNode(Opcodes.DALOAD)); // Load array index
            insnList.add(new InsnNode(Opcodes.D2F)); // Cast
            insnList.add(new VarInsnNode(Opcodes.FSTORE, 3)); // Save value to param 3

            setNormalNode.instructions.insertBefore(setNormalNode.instructions.get(0), insnList);
        }

        if (getVertexStateNode != null && addVertexNode != null && setNormalNode != null) {
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
