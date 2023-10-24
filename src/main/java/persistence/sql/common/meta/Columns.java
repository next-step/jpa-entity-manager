package persistence.sql.common.meta;

import java.lang.reflect.Field;
import java.util.Arrays;
import utils.StringUtils;

public class Columns {
    private final Column[] value;

    private Columns(Field[] fields) {
        this.value = Column.of(fields);
    }

    public static Columns of(Field[] fields) {
        return new Columns(fields);
    }

    /**
     * 칼럼명을 ','으로 이어 한 문자열로 반환합니다.
     * 예) "name, age, gender"
     */
    public String getColumnsWithComma() {
        return StringUtils.withComma(Arrays.stream(value)
            .map(Column::getName)
            .toArray(String[]::new));
    }
}
