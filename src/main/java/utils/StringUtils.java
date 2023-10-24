package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    public static String withComma(String[] input) {
        return String.join(", ", input);
    }

    /**
     * Object가 문자열인 경우 콜론과 함께 반환합니다.
     * 예) 'apple', '1'
     */
    public static String parseChar(Object value) {
        final String REGEX = "[-+]?\\d*\\.?\\d+";
        Pattern pattern = Pattern.compile(REGEX);

        String v = value.toString();
        String type = value.getClass().getSimpleName();

        if(!pattern.matcher(v).find()) {
            v = String.format("'%s'", value);
        }

        if(type.equals("String") || type.equals("char") || type.equals("Character")) {
            v = String.format("'%s'", value);
        }

        return v;
    }
}
