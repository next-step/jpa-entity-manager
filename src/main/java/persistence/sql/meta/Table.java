package persistence.sql.meta;

import jakarta.persistence.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.util.StringUtils;

public class Table {

    private final Class<?> clazz;
    private final Columns columns;
    private static final Map<Class<?>, Table> cashTable = new ConcurrentHashMap<>();

    private Table(Class<?> clazz, Columns columns) {
        this.clazz = clazz;
        this.columns = columns;
    }

    public static Table getInstance(Class<?> clazz) {
        if (cashTable.containsKey(clazz)) {
            return cashTable.get(clazz);
        }

        Columns columns = Columns.from(clazz.getDeclaredFields());
        validate(clazz, columns);

        return cashTable.computeIfAbsent(clazz, t -> new Table(clazz, columns));
    }

    public List<Column> getColumns() {
        return columns.getColumns();
    }

    public String getTableName() {
        jakarta.persistence.Table table = clazz.getAnnotation(jakarta.persistence.Table.class);
        if (table == null || StringUtils.isNullOrEmpty(table.name())) {
            return clazz.getSimpleName();
        }
        return table.name();
    }

    public List<Column> getInsertColumns() {
        return columns.getInsertColumns();
    }

    public List<Column> getUpdateColumns() {
        return columns.getUpdateColumns();
    }

    public Column getIdColumn() {
        return columns.getIdColumn();
    }

    public Object getIdValue(Object entity) {
        return columns.getIdValue(entity);
    }

    public void setIdValue(Object entity, Object id) {
        columns.getIdColumn().setFieldValue(entity, id);
    }

    private static void validate(Class<?> clazz, Columns columns) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("엔티티 객체가 아닙니다.");
        }

        long idFieldCount = columns.getIdCount();

        if (idFieldCount != 1) {
            throw new IllegalArgumentException("Id 필드는 필수로 1개를 가져야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(clazz, table.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }
}
