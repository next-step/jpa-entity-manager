package persistence.sql.mapping;

import persistence.sql.QueryException;
import persistence.sql.dml.QueryNumberValueBinder;
import persistence.sql.dml.QueryStringValueBinder;
import persistence.sql.dml.QueryValueBinder;

import java.io.Serializable;
import java.util.List;

public class Value implements Serializable {

    private static final List<QueryValueBinder> queryValueBinders = initQueryValueBinders();

    private final Class<?> originalType;

    private final int sqlType;

    private Object value;

    private String valueClause;

    public Value(final Class<?> originalType, final int sqlType) {
        this(originalType, sqlType, null, null);
    }

    public Value(final Class<?> originalType, final int sqlType, final Object value) {
        this(originalType, sqlType, value, null);
        this.valueClause = bindValueClause();
    }

    public Value(final Class<?> originalType, final int sqlType, final Object value, final String valueClause) {
        this.originalType = originalType;
        this.sqlType = sqlType;
        this.value = value;
        this.valueClause = valueClause;
    }

    private static List<QueryValueBinder> initQueryValueBinders() {
        return List.of(
                new QueryStringValueBinder(),
                new QueryNumberValueBinder()
        );
    }

    public Class<?> getOriginalType() {
        return this.originalType;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    public Object getValue() {
        return this.value;
    }

    public String getValueClause() {
        return (this.valueClause != null) ? this.valueClause : this.value.toString();
    }

    public void setValue(final Object value) {
        this.value = value;
        this.valueClause = bindValueClause();
    }

    public Value clone() {
        return new Value(this.originalType, this.sqlType, this.value, this.valueClause);
    }

    private String bindValueClause() {

        return queryValueBinders.stream()
                .filter(binder -> binder.support(this))
                .findFirst()
                .orElseThrow(() -> new QueryException("not found InsertQueryValueBinder for " + getOriginalType() + " type"))
                .bind(getValue());
    }

}
