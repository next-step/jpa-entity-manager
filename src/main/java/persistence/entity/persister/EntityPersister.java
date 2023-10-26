package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;

import java.lang.reflect.Field;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T insert(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());
        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        String sql = new InsertQueryBuilder().prepareStatement(entityAttribute, instance);

        if (idAttribute.getGenerationType() != null) {
            long key = jdbcTemplate.executeAndReturnGeneratedId(sql);
            try {
                setGeneratedIdToEntity(instance, idAttribute, key);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public <T> void remove(T entity, String id) {
        EntityAttribute entityAttribute = EntityAttribute.of(entity.getClass());
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        String deleteDML = deleteQueryBuilder.prepareStatement(entityAttribute, id);
        jdbcTemplate.execute(deleteDML);
    }

    private <T> void setGeneratedIdToEntity(T instance, IdAttribute idAttribute, long key) throws NoSuchFieldException {
        Field idField = idAttribute.getField();
        Class<?> idType = idField.getType();

        if (idType == Long.class) {
            setGeneratedIdToEntity(instance, idField, key);
        }
        if (idType == Integer.class) {
            setGeneratedIdToEntity(instance, idField, (int) key);
        }
    }

    private <T, U> void setGeneratedIdToEntity(T instance, Field idField, U key) {
        idField.setAccessible(true);
        try {
            idField.set(instance, key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("인스턴스의 ID 필드에 키 값을 할당하는데 실패했습니다.", e);
        }
    }
}
