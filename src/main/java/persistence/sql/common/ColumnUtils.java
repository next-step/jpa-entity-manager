package persistence.sql.common;

import jakarta.persistence.Id;
import persistence.sql.ddl.Column;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnUtils {
	public static List<Column> convertClassToColumnList(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				.map(Column::new)
				.collect(Collectors.toList());
	}

	public static Field findPrimaryKeyFromClass(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields())
				.filter(x -> x.isAnnotationPresent(Id.class))
				.findAny().get();
	}
}
