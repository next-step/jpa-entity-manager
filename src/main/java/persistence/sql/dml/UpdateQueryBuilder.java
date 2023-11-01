package persistence.sql.dml;

import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class UpdateQueryBuilder {
	private static final String UPDATE_COMMAND = "UPDATE %s SET %s;";

	public UpdateQueryBuilder() {
	}

	public String buildQuery(EntityMetadata entityMetadata) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityMetadata.buildSetClause());
	}

	public String buildQuery(EntityMetadata entityMetadata, WhereClauseBuilder whereClauseBuilder) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityMetadata.buildSetClause() + whereClauseBuilder.buildClause());
	}

	public String buildByIdQuery(EntityMetadata entityMetadata) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityMetadata.buildSetClause() + new WhereClauseBuilder(entityMetadata).buildPKClause());
	}

	public String buildByIdQuery(Columns columns, EntityMetadata entityMetadata) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), columns.buildSetClause() + new WhereClauseBuilder(entityMetadata).buildPKClause());
	}
}
