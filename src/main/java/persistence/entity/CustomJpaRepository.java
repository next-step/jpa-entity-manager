package persistence.entity;

import dialect.Dialect;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldValue;

import static utils.StringUtils.isBlankOrEmpty;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final Dialect dialect;
    private final EntityManager entityManager;

    public CustomJpaRepository(Dialect dialect, EntityManager entityManager) {
        this.dialect = dialect;
        this.entityManager = entityManager;
    }

    @Override
    public T save(T entity) {
        //dirty check -> merge -> detach
        if (isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }

    private boolean isNew(T entity) {
        try {
            FieldInfo idFieldData = new FieldInfos(entity.getClass().getDeclaredFields()).getIdFieldData();
            FieldValue idFieldValue = new FieldValue(dialect, idFieldData.getField(), entity);
            return idFieldValue == null || isBlankOrEmpty(idFieldValue.getValue());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
