package persistence.sql.metadata;

import java.lang.reflect.Field;

public class EntityValue {
	private final Column column;

	private final String value;

	public EntityValue(Field field, Object object) {
		this.column = new Column(field);

		try {
			field.setAccessible(true);
			this.value = convertValueToString(field, String.valueOf(field.get(object)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public EntityValue(Field field, String value) {
		this.column = new Column(field);
		this.value = convertValueToString(field, value);
	}

	public Column getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public boolean checkPossibleToBeValue() {
		if("null".equals(value) && !column.isNullable()) {
			return false;
		}

		return column.checkPossibleToBeValue();
	}

	private String convertValueToString(Field field, String value) {
		if("null".equals(value) && !column.isNullable()) {
			throw new IllegalArgumentException(column.getName() + "의 값은 NULL을 저장할 수 없습니다.");
		}

		if(field.getType().equals(String.class) && !"null".equals(value)) {
			value = "'" + value + "'";
		}

		return value;
	}
}
