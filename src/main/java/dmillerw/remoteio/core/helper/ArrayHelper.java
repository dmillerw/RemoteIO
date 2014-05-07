package dmillerw.remoteio.core.helper;

/**
 * @author dmillerw
 */
public class ArrayHelper {

	public static String toString(Object ... objects) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i=0; i<objects.length; i++) {
			Object obj = objects[i];
			sb.append("[" + i + "] ");
			sb.append(obj != null ? obj.toString() : "null");
			if (i != objects.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

}
