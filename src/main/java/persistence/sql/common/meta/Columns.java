package persistence.sql.common.meta;

import java.lang.reflect.Field;
import java.util.Arrays;
import persistence.exception.NotFoundIdException;
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
     * 칼럼 제약조건에 대해서 문자열로 반환합니다.
     */
    public String getConstraintsWithColumns() {
        return StringUtils.withComma(Arrays.stream(value)
            .map(column -> column.getName()
                + column.getType()
                + column.getConstraints().getNotNull()
                + column.getConstraints().getGeneratedValue())
            .toArray(String[]::new));
    }

    /**
     * 칼럼명을 ','으로 이어 한 문자열로 반환합니다. 예) "name, age, gender"
     */
    public String getColumnsWithComma() {
        return StringUtils.withComma(Arrays.stream(value)
            .map(Column::getName)
            .toArray(String[]::new));
    }

    /**
     * @return
     * @Id의 field 명을 가져온다.
     */
    public String getIdName() {
        return Arrays.stream(value)
            .filter(Column::isPrimaryKey)
            .findFirst()
            .orElseThrow(NotFoundIdException::new)
            .getName();
    }

    public String getIdFieldName() {
        return Arrays.stream(value)
                .filter(Column::isPrimaryKey)
                .findFirst()
                .orElseThrow(NotFoundIdException::new)
                .getFieldName();
    }

    public String getPrimaryKeyWithComma() {
        return StringUtils.withComma(Arrays.stream(value)
            .filter(Column::isPrimaryKey)
            .map(Column::getName)
            .toArray(String[]::new));
    }

    public Column[] getValue() {
        return value;
    }
}
