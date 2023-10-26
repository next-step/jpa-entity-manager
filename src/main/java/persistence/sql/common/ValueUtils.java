package persistence.sql.common;

import persistence.sql.dml.Value;
import persistence.sql.dml.Values;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ValueUtils {
	public static Values convertObjectToValues(Object object) {
		Values values = new Values(new ArrayList<>());
		Field[] fields = object.getClass().getDeclaredFields();

		for(Field field : fields) {
			field.setAccessible(true);
			values.addInsertValue(new Value(field, object));
		}

		return values;
	}
}
