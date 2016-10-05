package me.dmillerw.remoteio.asm.transformer;

import me.dmillerw.remoteio.asm.CoreLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * Created by dmillerw
 */
public class TransformEntityPlayer implements ITransformer {

    private static final String ON_UPDATE_OBF = "func_70071_h_";
    private static final String ON_UPDATE_DEOBF = "onUpdate";
    private static String onUpdate() {
        return CoreLoader.obfuscated ? ON_UPDATE_OBF : ON_UPDATE_DEOBF;
    }

    private static final String CAN_INTERACT_WITH_OBF = "func_75145_c";
    private static final String CAN_INTERACT_WITH_DEOBF = "canInteractWith";
    private static String canInteractWith() {
        return CoreLoader.obfuscated ? CAN_INTERACT_WITH_OBF : CAN_INTERACT_WITH_DEOBF;
    }

    @Override
    public String[] getClasses() {
        return new String[] {"net.minecraft.entity.player.EntityPlayer", "net.minecraft.entity.player.EntityPlayerMP"};
    }

    @Override
    public byte[] transform(String name, byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        MethodNode targetMethod = null;
        MethodInsnNode targetInsnNode = null;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(onUpdate())) {
                CoreLoader.logger.info("Found target method 'onUpdate'");
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractNode = iterator.next();

                    if (abstractNode instanceof MethodInsnNode) {
                        MethodInsnNode node = (MethodInsnNode) abstractNode;

                        if (node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                            if (node.name.equals(canInteractWith())) {
                                targetMethod = methodNode;
                                targetInsnNode = node;

                                CoreLoader.logger.info("Found target within method");

                                break;
                            }
                        }
                    }
                }
            }
        }

        if (targetMethod != null && targetInsnNode != null) {
            MethodInsnNode node = new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "me/dmillerw/remoteio/core/handler/ContainerHandler",
                    "canInteractWith",
                    "(Lnet/minecraft/inventory/Container;Lnet/minecraft/entity/player/EntityPlayer;)Z",
                    false
            );

            targetMethod.instructions.insertBefore(targetInsnNode, node);
            targetMethod.instructions.remove(targetInsnNode);

            CoreLoader.logger.info("Successfully transformed EntityPlayer.class!");

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            CoreLoader.logger.warn("Failed to transform EntityPlayer.class!");
            return basicClass;
        }
    }
}
