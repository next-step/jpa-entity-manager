package persistence.sql.meta.simple;

import jakarta.persistence.GenerationType;
import persistence.sql.meta.PrimaryKey;

public class SimplePrimaryKey implements PrimaryKey {

    private SimpleColumn primaryKey;

    public SimplePrimaryKey(final SimpleColumn primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String name() {
        return this.primaryKey.getFieldName();
    }

    @Override
    public String value(final Object object) {
        return this.primaryKey.value(object);
    }

    @Override
    public Class<?> type() {
        return this.primaryKey.type();
    }

    @Override
    public GenerationType strategy() {
        return this.primaryKey.generateType();
    }

}
