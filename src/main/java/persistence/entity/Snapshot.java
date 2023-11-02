package persistence.entity;

import jakarta.persistence.Transient;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class Snapshot {
	private final Field[] fields;

	private final Object entity;

	public Snapshot(Object entity) {
		try {
			Constructor<?> constructor = entity.getClass().getDeclaredConstructor();
			constructor.setAccessible(true);

			Object copyEntity = constructor.newInstance();

			this.fields = Arrays.stream(entity.getClass().getDeclaredFields())
					.filter(x -> !x.isAnnotationPresent(Transient.class))
					.toArray(Field[]::new);

			for (Field field : fields) {
				field.setAccessible(true);
				field.set(copyEntity, field.get(entity));
			}

			this.entity = copyEntity;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Field[] getChangedColumns(Object newEntity) {
		return Arrays.stream(fields)
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
