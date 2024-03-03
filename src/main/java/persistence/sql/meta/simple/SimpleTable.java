package persistence.sql.meta.simple;

import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.Table;


public class SimpleTable implements Table {

    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;


    public SimpleTable(final String tableName, final PrimaryKey primaryKey, final Columns columns) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
    }

    @Override
    public String name() {
        return this.tableName;
    }

    @Override
    public PrimaryKey primaryKey() {
        return this.primaryKey;
    }
    @Override
    public Columns columns() {
        return this.columns;
    }

}

