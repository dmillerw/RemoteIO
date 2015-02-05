package remoteio.common.lib;

public class Strings{
    public static String wrap(String text, int len){
        StringBuilder builder = new StringBuilder(text);
        int i = 0;
        while(i + len < builder.length() && (i = builder.lastIndexOf(" ", i + len)) != -1){
            builder.replace(i, i + 1, "\n");
        }
        return builder.toString();
    }
}