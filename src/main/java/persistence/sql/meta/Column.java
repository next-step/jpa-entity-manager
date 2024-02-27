package persistence.sql.meta;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import static persistence.sql.meta.parser.ValueParser.valueParse;

public interface Column {
    public String getFieldName();

    public String value(Object object);

    public Class<?> type();

    public GenerationType generateType();

    public boolean isNullable();
}
