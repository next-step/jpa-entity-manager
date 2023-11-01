package persistence.entity;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;


public class Snapshot {
	private final Field[] fields;

	private final Object entity;

	public Snapshot(Object entity) {
		this.entity = entity;
		this.fields = entity.getClass().getDeclaredFields();
	}

	public Field[] getChangedColumns(Object newEntity) {
		return Arrays.stream(fields)
				.filter(x -> !x.isAnnotationPresent(Transient.class))
				.filter(x -> {
					x.setAccessible(true);
					try {
						return x.get(entity) != x.get(newEntity);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.toArray(Field[]::new);
	}
}
