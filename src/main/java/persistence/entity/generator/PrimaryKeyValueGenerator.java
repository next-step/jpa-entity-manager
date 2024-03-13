package persistence.entity.generator;

import jakarta.persistence.Id;
import persistence.entity.exception.InvalidPrimaryKeyException;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PrimaryKeyValueGenerator {
    public static Long primaryKeyValue(Object entity) {
        Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(InvalidPrimaryKeyException::new);

        idField.setAccessible(true);
        try {
            return (Long) idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new InvalidPrimaryKeyException();
        }
    }
}
