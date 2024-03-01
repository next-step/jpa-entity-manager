package persistence.entity.context;

public class ObjectNotFoundException extends RuntimeException {

    private final Class<?> entityClass;
    private final Long id;

    public ObjectNotFoundException(Class<?> entityClass, Long id) {
        this.entityClass = entityClass;
        this.id = id;
    }
}
