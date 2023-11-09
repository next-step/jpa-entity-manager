package persistence.entity.loader;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;

public class EntityLoader {
    public <T> T mapToEntity(Class<T> cls, ResultSet resultSet) {
        T entity = createInstance(cls);
        return entity;
    }

    private <T> T createInstance(Class<T> cls) {
        try{
            return cls.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
