package persistence;

import java.util.Objects;

public class EntityInfo<T> {

    private final Object id;
    private final Class<T> clazz;

    private EntityInfo(Object id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public static <T> EntityInfo<T> createEntityInfo(Object id, Class<T> clazz) {
        return new EntityInfo<>(id, clazz);
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // 같은 객체인지 확인
        if (!(obj instanceof EntityInfo)) return false; // 타입 확인
        EntityInfo<?> other = (EntityInfo<?>) obj; // 형변환
        return Objects.equals(id, other.id) && clazz.equals(other.clazz); // id와 clazz 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz); // id와 clazz의 해시 코드 반환
    }
}
