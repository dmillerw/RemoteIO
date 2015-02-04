package remoteio.core.transform;

import remoteio.core.MappingHelper;
import remoteio.core.mapping.MappingConstants;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * @author dmillerw
 */
public class TransformTessellator implements ITransformer {

    private static final String METHOD_HANDLER = "remoteio/client/render/TessellatorCapture";

    @Override
    public String[] getClasses() {
        return new String[] {"net.minecraft.client.renderer.Tessellator"};
    }

    @Override
    public byte[] transform(String name, byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode getVertexStateNode = null;
        MethodNode addVertexNode = null;
        MethodNode setNormalNode = null;

        final String tessellator = MappingConstants.Type.get(MappingConstants.Type.TESSELLATOR);
        final String vertexState = MappingConstants.Type.get(MappingConstants.Type.TESSELLATOR_VERTEX_STATE);

        for (MethodNode methodNode : classNode.methods) {
            if (MappingConstants.Method.equals(methodNode, MappingConstants.Method.GET_VERTEX_STATE, MappingConstants.Method.Desc.GET_VERTEX_STATE)) {
                getVertexStateNode = methodNode;
                MappingHelper.logger.info("Found method 'getVertexState'");
            } else if (MappingConstants.Method.equals(methodNode, MappingConstants.Method.ADD_VERTEX, MappingConstants.Method.Desc.ADD_VERTEX)) {
                addVertexNode = methodNode;
                MappingHelper.logger.info("Found method 'addVertex'");
            } else if (MappingConstants.Method.equals(methodNode, MappingConstants.Method.SET_NORMAL, MappingConstants.Method.Desc.SET_NORMAL)) {
                setNormalNode = methodNode;
                MappingHelper.logger.info("Found method 'setNormal'");
            }
        }

        if (getVertexStateNode != null) {
            InsnList insnList = new InsnList();

            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Add this (tessellator instance) to stack
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.RAW_BUFFER_INDEX, "I")); // add rawBufferIndex variable
            insnList.add(new InsnNode(Opcodes.ICONST_1)); // add number 1 to stack
            LabelNode l1 = new LabelNode(new Label());
            insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, l1)); // if rawBufferIndex is less than or equal to 1
            insnList.add(new TypeInsnNode(Opcodes.NEW, vertexState)); // create new vertex state object
            insnList.add(new InsnNode(Opcodes.DUP)); // duplicate top object on stack
            insnList.add(new InsnNode(Opcodes.ICONST_0)); // add number 0 to stack
            insnList.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT)); // create new array of type int with size 0

            // Add important variables to stack
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.RAW_BUFFER_INDEX, "I"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.VERTEX_COUNT, "I"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.HAS_TEXTURE, "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.HAS_BRIGHTNESS, "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.HAS_NORMALS, "Z"));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
            insnList.add(getFieldNode(tessellator, MappingConstants.Field.HAS_COLOR, "Z"));

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
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, METHOD_HANDLER, "rotatePointWithOffset", "(DDD)[D", false));

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
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, METHOD_HANDLER, "rotatePoint", "(DDD)[D", false));

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
            MappingHelper.logger.warn("Successfully transformed Tessellator.class!");
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            MappingHelper.logger.warn("Failed to transform Tessellator.class!");
            return basicClass;
        }
    }

    private FieldInsnNode getFieldNode(String owner, String[] name, String desc) {
        if (MappingHelper.obfuscated) {
            return new FieldInsnNode(Opcodes.GETFIELD, owner, name[2], desc);
        } else {
            return new FieldInsnNode(Opcodes.GETFIELD, owner, name[0], desc);
        }
    }
}
