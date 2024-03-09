package pojo;

import jakarta.persistence.Table;

/**
 * 테이블 정보
 */
public class TableInfo {

    private final String name;

    public TableInfo(Class<?> clazz) {
        this.name = getTableName(clazz);
    }

    public String getName() {
        return name;
    }

    private String getTableName(Class<?> clazz) {
        return clazz.isAnnotationPresent(Table.class) ? clazz.getAnnotation(Table.class).name()
                : clazz.getSimpleName().toLowerCase();
    }
}
