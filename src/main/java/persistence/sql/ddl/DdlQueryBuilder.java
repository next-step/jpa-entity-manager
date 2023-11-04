package persistence.sql.ddl;

import persistence.dialect.DbType;
import persistence.dialect.Dialect;

public abstract class DdlQueryBuilder {
	protected final Dialect dialect;

	public DdlQueryBuilder(DbType dbType) {
		dialect = dbType.createDialect();
	}
}
