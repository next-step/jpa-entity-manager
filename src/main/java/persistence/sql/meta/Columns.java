package persistence.sql.meta;

import java.util.List;

public interface Columns {

    List<String> names();

    List<String> values();

    List<? extends Column> getColumns();
}
