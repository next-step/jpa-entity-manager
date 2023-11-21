package persistence.sql.usecase;

import java.lang.reflect.InvocationTargetException;
import persistence.sql.vo.DatabaseFields;

public class CreateSnapShotObject {
    private final GetFieldFromClass getFieldFromClass = new GetFieldFromClass();
    private final GetFieldValue getFieldValue = new GetFieldValue();
    private final SetFieldValue setFieldValue = new SetFieldValue();

    public CreateSnapShotObject() {
    }

    public Object execute(Object object) {
        Object newInstance = createInstance(object.getClass());
        DatabaseFields databaseFields = getFieldFromClass.execute(object.getClass());
        databaseFields.getDatabaseFields().forEach(
            field -> {
                Object value = getFieldValue.execute(object, field);
                setFieldValue.execute(newInstance, field, value);
            }
        );
        return newInstance;
    }

    private Object createInstance(Class<?> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Can't create instance with class " + cls.getSimpleName());
        }
    }
}
