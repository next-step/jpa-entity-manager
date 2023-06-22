package persistence.sql;

import java.util.Collection;

public abstract class QueryBuilder {
    protected static final String BLANK = " ";
    protected static final String COMMA = ",";

    protected final Class<?> clazz;
    protected final Id id;
    protected final Columns columns;

    protected <T> QueryBuilder(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalStateException("Entity is not set");
        }
        this.clazz = clazz;
        this.id = new Id(clazz);
        this.columns = new Columns(clazz);
    }

    protected String getTableName() {
        return EntityUtils.getName(clazz);
    }

    protected String joinWithComma(Collection<String> fields) {
        return String.join(COMMA, fields);
    }
}
