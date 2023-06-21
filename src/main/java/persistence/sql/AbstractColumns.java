package persistence.sql;

import java.lang.reflect.Field;
import java.util.*;

import static persistence.sql.EntityUtils.getColumnName;

public abstract class AbstractColumns {
    protected final Map<String, Field> columns = new LinkedHashMap<>();

    public AbstractColumns(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            this.addColumns(field);
        }
    }

    private void addColumns(Field field) {
        if (addable(field)) {
            columns.put(getColumnName(field), field);
        }
    }

    protected abstract boolean addable(Field field);

    public Set<String> getColumnNames() {
        return columns.keySet();
    }

    public Map<String, Field> getColumns() {
        return new LinkedHashMap<>(columns);
    }

    public List<Field> getColumnValues() {
        return new ArrayList<>(columns.values());
    }
}
