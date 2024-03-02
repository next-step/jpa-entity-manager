package persistence.sql.ddl;

import persistence.sql.mapping.TableData;

public class DropQueryBuilder {
    private final TableData tableData;

    public DropQueryBuilder(Class<?> clazz) {
        this.tableData = TableData.from(clazz);
    }

    public String build() {
        return String.format("DROP TABLE %s", tableData.getName());
    }
}
