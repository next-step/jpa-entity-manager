package persistence.sql.dml.column;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.common.Fields;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DmlColumns {
    private static final String DELIMITER = ", ";

    private final List<DmlColumn> dmlColumns;

    public DmlColumns(List<DmlColumn> dmlColumns) {
        this.dmlColumns = Collections.unmodifiableList(dmlColumns);
    }

    public static DmlColumns of(Object object) {
        Fields fields = Fields.of(object.getClass());
        List<Field> dmlFields = fields.getFields(Id.class, Transient.class);

        List<DmlColumn> columns = dmlFields.stream()
                .map(field -> new DmlColumn(field, object))
                .collect(Collectors.toList());

        return new DmlColumns(columns);
    }

    public String getNames() {
        return dmlColumns.stream()
                .map(DmlColumn::name)
                .collect(Collectors.joining(DELIMITER));
    }

    public String getValues() {
        return dmlColumns.stream()
                .map(DmlColumn::value)
                .collect(Collectors.joining(DELIMITER));
    }

    public List<DmlColumn> getDmlColumns() {
        return dmlColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DmlColumns that = (DmlColumns) o;
        return Objects.equals(dmlColumns, that.dmlColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dmlColumns);
    }
}
