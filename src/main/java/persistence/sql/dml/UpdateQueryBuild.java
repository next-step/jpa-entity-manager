package persistence.sql.dml;

import persistence.sql.domain.Query;

public interface UpdateQueryBuild {

    Query update(Object entity);
}
