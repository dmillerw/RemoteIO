package remoteio.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author dmillerw
 */
public class MappingHelper {
    public static Logger logger = LogManager.getLogger("RemoteIO:ASM");

    public static boolean obfuscated;

    public static boolean stringMatches(String string, String ... compare) {
        for (String s : compare) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }

}
