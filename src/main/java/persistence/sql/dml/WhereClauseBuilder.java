package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class WhereClauseBuilder {
	private static final String WHERE_CLAUSE = " WHERE %s";

	private final EntityMetadata entityMetadata;

	public WhereClauseBuilder(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}

	public String buildClause() {
		if(entityMetadata.buildWhereClause().isEmpty()) {
			return "";
		}

		return format(WHERE_CLAUSE, entityMetadata.buildWhereClause());
	}

	public String buildPKClause() {
		return format(WHERE_CLAUSE, entityMetadata.buildWhereWithPKClause());
	}
}
