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

    private <T> boolean isTable(Class<T> tClass) {
        return tClass.isAnnotationPresent(Table.class);
    }

    private <T> boolean isNotEntity(Class<T> tClass) {
        return !tClass.isAnnotationPresent(Entity.class);
    }

    private <T> String getTableName(Class<T> tClass) {
        String tableName = tClass.getSimpleName();

        if (isTable(tClass) && !"".equals(tClass.getAnnotation(Table.class).name())) {
            tableName = tClass.getAnnotation(Table.class).name();
        }

        return tableName;
    }

    public String getValue() {
        return value;
    }
}
