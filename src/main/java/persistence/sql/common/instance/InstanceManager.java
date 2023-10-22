package persistence.sql.common.instance;

import utils.StringUtils;

import java.util.Arrays;

public class InstanceManager {
    /**
     * 값을 ','으로 이어 한 문자열로 반환합니다.
     * 예) "홍길동, 13, F"
     */
    public static String getValuesWithComma(Value[] values) {
        return StringUtils.withComma(Arrays.stream(values)
                .map(Value::getValue)
                .toArray(String[]::new));
    }
}
