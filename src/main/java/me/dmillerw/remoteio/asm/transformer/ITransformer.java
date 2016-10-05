package me.dmillerw.remoteio.asm.transformer;

/**
 * @author dmillerw
 */
public interface ITransformer {

    public String[] getClasses();

    public byte[] transform(String name, byte[] basicClass);
}