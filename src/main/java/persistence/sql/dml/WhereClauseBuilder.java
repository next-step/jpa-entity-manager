package persistence.sql.dml;

import persistence.sql.metadata.EntityValue;
import persistence.sql.metadata.EntityValues;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class WhereClauseBuilder {
	private static final String WHERE_CLAUSE = " WHERE %s";

	private final EntityValues entityValues;

	public WhereClauseBuilder(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();

		List<EntityValue> list = Arrays.stream(fields)
				.map(x -> new EntityValue(x, object))
				.filter(x -> !x.getColumn().isTransient())
				.collect(Collectors.toList());

		this.entityValues = new EntityValues(list);
	}

	public WhereClauseBuilder(EntityValues entityValues) {
		this.entityValues = entityValues;
	}

	public String buildClause() {
		if(entityValues.buildWhereClause().isEmpty()) {
			return "";
		}

		return format(WHERE_CLAUSE, entityValues.buildWhereClause());
	}

	public String buildPKClause() {
		return format(WHERE_CLAUSE, entityValues.buildWherePKClause());
	}
}
