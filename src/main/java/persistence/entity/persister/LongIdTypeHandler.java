package persistence.entity.persister;

import java.lang.reflect.Field;

class LongIdTypeHandler implements IdTypeHandler {
    @Override
    public boolean support(Class<?> idType) {
        return idType == Long.class;
    }

    @Override
    public <T> void setGeneratedIdToEntity(T instance, Field idField, long key) {
        idField.setAccessible(true);
        try {
            idField.set(instance, key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Long 타입의 ID 필드에 키 값을 할당하는데 실패했습니다.", e);
        }
    }
}
