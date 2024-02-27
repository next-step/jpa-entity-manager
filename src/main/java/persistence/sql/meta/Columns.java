package persistence.sql.meta;

import java.util.List;

public interface Columns {

    List<String> names();

    List<String> values(Object object);

    List<Column> getColumns();
}
