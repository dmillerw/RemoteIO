package me.dmillerw.remoteio.asm.transformer;

/**
 * @author dmillerw
 */
public interface ITransformer {

    String[] getClasses();

    byte[] transform(String name, byte[] basicClass);
}