package persistence.sql.common.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import persistence.exception.InvalidEntityException;

public class TableName {

    private final String value;

    private <T> TableName(Class<T> tClass) {
        this.value = parseTableName(tClass);
    }

    public static <T> TableName of(Class<T> tClass) {
        return new TableName(tClass);
    }

    /**
     * class의 이름을 가져와 table 이름으로 설정합니다.
     */
    private <T> String parseTableName(Class<T> tClass) {
        if (isNotEntity(tClass)) {
            throw new InvalidEntityException();
        }

        return getTableName(tClass);
    }

    /**
     * @Table이 존재하는지 여부 확인
     */
    private <T> boolean isTable(Class<T> tClass) {
        return tClass.isAnnotationPresent(Table.class);
    }

    /**
     * @Table에 name 옵션이 존재한지 여부 확인
     */
    private <T> boolean isTableAndValue(Class<T> tClass) {
        return isTable(tClass) && !"".equals(tClass.getAnnotation(Table.class).name());
    }

    /**
     * @Entity가 존재하지 않는지 확인
     */
    private <T> boolean isNotEntity(Class<T> tClass) {
        return !tClass.isAnnotationPresent(Entity.class);
    }

    /**
     * @Table의 name 값을 가져옵니다.
     */
    private <T> String getTableName(Class<T> tClass) {
        if (isTableAndValue(tClass)) {
            return tClass.getAnnotation(Table.class).name();
        }

        return tClass.getSimpleName();
    }

    public String getValue() {
        return value;
    }
}
