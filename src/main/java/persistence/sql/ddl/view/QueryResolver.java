package persistence.sql.ddl.view;

import persistence.sql.domain.ColumnOperation;

import java.util.List;

public interface QueryResolver {

    String toQuery(List<ColumnOperation> columns);
}
