package persistence.entity;

import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;

import static utils.StringUtils.isBlankOrEmpty;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
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
            Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
            IdField idField = new IdField(field, entity);
            return idField.getFieldInfoTemp().getFieldValue() == null
                    || isBlankOrEmpty(idField.getFieldInfoTemp().getFieldValue().getValue());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
