package persistence.sql.ddl;

import persistence.dialect.DbType;
import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class H2DdlQueryBuilder extends DdlQueryBuilder{
	private static final String CREATE_TABLE_COMMAND = "CREATE TABLE %s";

	private static final String DROP_TABLE_COMMAND = "DROP TABLE %s IF EXISTS;";

	private H2DdlQueryBuilder() {
		super(DbType.H2);
	}

	public static H2DdlQueryBuilder build() {
		return new H2DdlQueryBuilder();
	}

	public String createQuery(EntityMetadata entityMetadata) {
		return format(CREATE_TABLE_COMMAND,
				entityMetadata.getTableName() +
						"(" +
						entityMetadata.getColumnsToCreate(dialect) +
						");"
		);
	}

	public String dropQuery(EntityMetadata entityMetadata) {
		return format(DROP_TABLE_COMMAND, entityMetadata.getTableName());
	}
}
