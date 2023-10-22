package utils;

public final class StringUtils {
    public static String withComma(String[] input) {
        return String.join(", ", input);
    }

    /**
     * Object가 문자열인 경우 콜론과 함께 반환합니다.
     * 예) 'apple', '1'
     */
    public static String parseChar(Object value) {
        String v = value.toString();
        String type = value.getClass().getSimpleName();

        if(!value.toString().matches("[-+]?\\d*\\.?\\d+")) {
            v = String.format("'%s'", value);
        }

        if(type.equals("String") || type.equals("char") || type.equals("Character")) {
            v = String.format("'%s'", value);
        }

        return v;
    }
}
