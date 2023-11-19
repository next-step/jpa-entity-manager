package persistence.sql.usecase;

import java.lang.reflect.InvocationTargetException;
import persistence.sql.vo.DatabaseFields;

public class CreateSnapShotObject {
    private final GetFieldFromClassUseCase getFieldFromClassUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;
    private final SetFieldValueUseCase setFieldValueUseCase;

    public CreateSnapShotObject(GetFieldFromClassUseCase getFieldFromClassUseCase, GetFieldValueUseCase getFieldValueUseCase, SetFieldValueUseCase setFieldValueUseCase) {
        this.getFieldFromClassUseCase = getFieldFromClassUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
        this.setFieldValueUseCase = setFieldValueUseCase;
    }

    public Object execute(Object object) {
        Object newInstance = createInstance(object.getClass());
        DatabaseFields databaseFields = getFieldFromClassUseCase.execute(object.getClass());
        databaseFields.getDatabaseFields().forEach(
            field -> {
                Object value = getFieldValueUseCase.execute(object, field);
                setFieldValueUseCase.execute(newInstance, field, value);
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
