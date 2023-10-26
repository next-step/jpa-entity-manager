package persistence.sql.ddl;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.Query;

public class DmlQuery {
    private static final DmlQuery INSTANCE = new DmlQuery();

    private final CreateQuery createQuery;
    private final DropQuery dropQuery;

    private DmlQuery() {
        this.createQuery = new CreateQuery();
        this.dropQuery = new DropQuery();
    }

    public static DmlQuery getInstance() {
        return INSTANCE;
    }

    public String create(TableName tableName, Columns columns) {
       return INSTANCE.createQuery.get(tableName, columns);
    }

    public String drop(TableName tableName) {
        return INSTANCE.dropQuery.get(tableName);
    }
}
