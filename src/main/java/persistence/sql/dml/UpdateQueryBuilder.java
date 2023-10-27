package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.EntityValues;

import static java.lang.String.format;

public class UpdateQueryBuilder {
	private static final String UPDATE_COMMAND = "UPDATE %s SET %s;";

	public UpdateQueryBuilder() {

	}

	public String buildQuery(EntityMetadata entityMetadata, EntityValues entityValues) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityValues.buildSetClause());
	}

	public String buildQuery(EntityMetadata entityMetadata, EntityValues entityValues, WhereClauseBuilder whereClauseBuilder) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityValues.buildSetClause() + whereClauseBuilder.buildClause());
	}

	public String buildByIdQuery(EntityMetadata entityMetadata, EntityValues entityValues, WhereClauseBuilder whereClauseBuilder) {
		return format(UPDATE_COMMAND, entityMetadata.getTableName(), entityValues.buildSetClause() + whereClauseBuilder.buildPKClause());
	}
}
