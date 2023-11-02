package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class SelectQueryBuilder {
	private static final String SELECT_COMMAND = "SELECT %s FROM %s;";

	public SelectQueryBuilder() {
	}

	public String buildQuery(EntityMetadata entityMetadata) {
		return format(SELECT_COMMAND, "*", entityMetadata.getTableName());
	}

	public String buildQuery(EntityMetadata entityMetadata, WhereClauseBuilder whereClauseBuilder) {
		return format(SELECT_COMMAND, "*", entityMetadata.getTableName() + whereClauseBuilder.buildClause());
	}

	public String buildFindByIdQuery(Class<?> clazz, String value) {
		EntityMetadata entityMetadata = new EntityMetadata(
				new Table(clazz)
				, new Columns(
						Arrays.stream(clazz.getDeclaredFields())
								.map(x -> new Column(x, value))
								.collect(Collectors.toList())
				)
		);
		return format(SELECT_COMMAND, "*", entityMetadata.getTableName() + new WhereClauseBuilder(entityMetadata).buildPKClause());
	}
}
