package persistence.sql.ddl.clause.column;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class ColumnClause {
    private static final Map<Type, Function<String, String>> typeToSqlConverter = Map.of(
            String.class, fieldName -> String.format("%s VARCHAR(30)", fieldName),
            Integer.class, fieldName -> String.format("%s INT", fieldName),
            int.class, fieldName -> String.format("%s INT", fieldName)
    );
    private final ColumnSpec spec;
    private final NullClause nullClause;

    public ColumnClause(Field field) {
        this.spec = new ColumnSpec(field);
        this.nullClause = new NullClause(field);
    }

    public String getQuery() {
        return typeToSqlConverter.get(this.spec.type()).apply(this.spec.name()) + " " + nullClause.getQuery();
    }

    public String name() {
        return this.spec.name();
    }
}
