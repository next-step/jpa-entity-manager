package persistence.sql.common.instance;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    /**
     * @Id, @Transient를 제외한 모둔 field를 가져와 [ 필드명 = 값 ] 형식으로 묶어 하나의 문자열로 반환합니다.
     * 예) "name = '홍길동', age = 13, gender = 'F'"
     */
    public static <T> String getFieldNameAndValue(T t) {
        return Arrays.stream(t.getClass().getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> {
                    String fieldName = field.getName();

                    if (field.isAnnotationPresent(Column.class)
                            && !"".equals(field.getDeclaredAnnotation(Column.class).name())) {
                        fieldName = field.getDeclaredAnnotation(Column.class).name();
                    }

                    return String.format("%s = %s", fieldName, StringUtils.parseChar(Value.extractValue(t, field)));
                })
                .collect(Collectors.joining(", "));

    }
}
