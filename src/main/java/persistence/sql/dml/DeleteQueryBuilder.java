package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class DeleteQueryBuilder {
	private static final String DELETE_COMMAND = "DELETE FROM %s;";

	public DeleteQueryBuilder() {
	}

	public String buildQuery(EntityMetadata entityMetadata) {
		return format(DELETE_COMMAND, entityMetadata.getTableName());
	}

	public String buildQuery(EntityMetadata entityMetadata, WhereClauseBuilder whereClauseBuilder) {
		return format(DELETE_COMMAND, entityMetadata.getTableName() + whereClauseBuilder.buildClause());
	}

	public String buildByIdQuery(EntityMetadata entityMetadata) {
		return format(DELETE_COMMAND, entityMetadata.getTableName() + new WhereClauseBuilder(entityMetadata).buildPKClause());
	}
}
