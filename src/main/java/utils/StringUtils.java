package utils;

public class StringUtils {

    private StringUtils() {

    }

    public static boolean isBlankOrEmpty(String target) {
        return target == null || target.isBlank() || target.isEmpty();
    }
}
