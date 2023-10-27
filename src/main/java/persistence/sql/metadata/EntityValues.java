package persistence.sql.metadata;

import java.util.List;
import java.util.stream.Collectors;

public class EntityValues {
	private final List<EntityValue> entityValues;

	public EntityValues(List<EntityValue> entityValues) {
		this.entityValues = entityValues;
	}

	public String buildValueClause() {
		return entityValues.stream()
				.map(EntityValue::getValue)
				.collect(Collectors.joining(","));
	}

	public String buildColumnsClause() {
		return new Columns(
				entityValues.stream()
				.map(EntityValue::getColumn)
				.collect(Collectors.toList())
			).buildColumnsToInsert();
	}

	public String buildSetClause() {
		return entityValues.stream()
				.map(x -> x.getColumn().getName() + " = " + x.getValue())
				.collect(Collectors.joining(", "));
	}

	public String buildWhereClause() {
		return entityValues.stream()
				.map(x -> x.getColumn().getName() + " = " + x.getValue())
				.collect(Collectors.joining(" AND "));
	}

	public String buildWherePKClause() {
		return entityValues.stream()
				.filter(x -> x.getColumn().isPrimaryKey())
				.map(x -> x.getColumn().getName() + " = " + x.getValue())
				.findFirst().get();
	}
}
