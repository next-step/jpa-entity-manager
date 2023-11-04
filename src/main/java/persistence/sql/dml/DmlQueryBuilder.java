package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DmlQueryBuilder {
	private static final String INSERT_COMMAND = "INSERT INTO %s (%s) VALUES %s;";

	private static final String DELETE_COMMAND = "DELETE FROM %s;";

	private static final String UPDATE_COMMAND = "UPDATE %s SET %s;";

	private static final String SELECT_COMMAND = "SELECT %s FROM %s;";

	private static final String WHERE_CLAUSE = " WHERE %s";

	private DmlQueryBuilder() {

	}

	public static DmlQueryBuilder build() {
		return new DmlQueryBuilder();
	}

	public String insertQuery(EntityMetadata entityMetadata) {
		if(entityMetadata == null) {
			throw new IllegalArgumentException("등록하려는 객체가 NULL 값이 될 수 없습니다.");
		}

		return format(INSERT_COMMAND,
				entityMetadata.getTableName(),
				entityMetadata.buildColumnsClause(),
				"(" +  entityMetadata.buildValueClause() + ")"
		);
	}

	public String deleteQuery(EntityMetadata entityMetadata) {
		return format(DELETE_COMMAND,
				entityMetadata.getTableName() + wherePKClause(entityMetadata)
		);
	}

	public String updateQuery(Columns columns, EntityMetadata entityMetadata) {
		return format(UPDATE_COMMAND,
				entityMetadata.getTableName(),
				columns.buildSetClause() + wherePKClause(entityMetadata)
		);
	}

	public String selectQuery(Class<?> clazz, String value) {
		EntityMetadata entityMetadata = new EntityMetadata(
				new Table(clazz)
				, new Columns(
				Arrays.stream(clazz.getDeclaredFields())
						.map(x -> new Column(x, value))
						.collect(Collectors.toList())
		)
		);

		return format(SELECT_COMMAND, "*", entityMetadata.getTableName() + wherePKClause(entityMetadata));
	}

	public String whereClause(EntityMetadata entityMetadata) {
		if(entityMetadata.buildWhereClause().isEmpty()) {
			return "";
		}

		return format(WHERE_CLAUSE, entityMetadata.buildWhereClause());
	}

	public String wherePKClause(EntityMetadata entityMetadata) {
		return format(WHERE_CLAUSE, entityMetadata.buildWhereWithPKClause());
	}
}
