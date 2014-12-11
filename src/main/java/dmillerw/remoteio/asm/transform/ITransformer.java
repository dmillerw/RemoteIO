package dmillerw.remoteio.asm.transform;

/**
 * @author dmillerw
 */
public interface ITransformer {

    public String[] getClasses();

    public byte[] transform(String name, byte[] basicClass);
}
