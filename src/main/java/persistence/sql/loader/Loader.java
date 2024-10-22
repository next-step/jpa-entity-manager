package persistence.sql.loader;

import java.util.List;

public interface Loader<T> {

    T load(Object primaryKey);

    List<T> loadAll();
}
