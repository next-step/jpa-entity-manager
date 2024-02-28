package persistence.sql.meta.simple;

import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.TableName;

public class SimpleEntityMetaCreator implements EntityMetaCreator {

    private final Class<?> clazz;

    private SimpleEntityMetaCreator(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static SimpleEntityMetaCreator of(Class<?> clazz) {
        return new SimpleEntityMetaCreator(clazz);
    }

    @Override
    public TableName createTableName() {
        return SimpleTableName.of(this.clazz);
    }

    @Override
    public PrimaryKey createPrimaryKey() {
        return SimplePrimaryKey.of(this.clazz);
    }

    @Override
    public Columns createColumns() {
        return SimpleColumns.of(this.clazz);
    }
}

