package persistence.sql.meta.simple;

import jakarta.persistence.GenerationType;
import persistence.sql.meta.Column;

public class SimpleColumn implements Column {

    private final String name;
    private final boolean isNullable;
    private final GenerationType generationType;
    private final Class<?> type;
    private SimpleValue simpleValue;


    public SimpleColumn(final String name, final boolean isNullable, final GenerationType generationType,
                        final Class<?> type) {
        this.name = name;
        this.isNullable = isNullable;
        this.generationType = generationType;
        this.type = type;
    }

    public SimpleColumn(final String name, final boolean isNullable, final GenerationType generationType,
                        final Class<?> type,final SimpleValue simpleValue) {
        this.name = name;
        this.isNullable = isNullable;
        this.generationType = generationType;
        this.type = type;
        this.simpleValue = simpleValue;
    }

    @Override
    public String getFieldName() {
        return this.name;
    }

    @Override
    public String value(final Object object) {
        return this.simpleValue.getValue();
    }

    @Override
    public Class<?> type()  {
        return this.type;
    }

    @Override
    public GenerationType generateType() {
        return this.generationType;
    }

    @Override
    public boolean isNullable() {
        return this.isNullable;
    }
}
