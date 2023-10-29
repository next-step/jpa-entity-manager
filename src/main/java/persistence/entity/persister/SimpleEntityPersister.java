package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.resolver.IdAttributeResolver;
import persistence.entity.loader.EntityLoader;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;

import java.lang.reflect.Field;

import static persistence.entity.attribute.resolver.AttributeHolder.ID_ATTRIBUTE_RESOLVERS;

public class SimpleEntityPersister implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final EntityLoader entityLoader;

    public SimpleEntityPersister(JdbcTemplate jdbcTemplate, EntityLoader entityLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T load(Class<T> clazz, String id) {
        return entityLoader.load(clazz, id);
    }

    @Override
    public <T> T update(T old, T updated) {
        EntityAttribute entityAttribute = EntityAttribute.of(old.getClass());

        String sql = UpdateQueryBuilder.of(old, updated, entityAttribute).prepareStatement();

        if (sql != null && !sql.isEmpty()) {
            jdbcTemplate.execute(sql);
        }

        return updated;
    }

    @Override
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

    @Override
    public <T> void remove(T instance, String id) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        String deleteDML = deleteQueryBuilder.prepareStatement(entityAttribute, id);
        jdbcTemplate.execute(deleteDML);
    }

    private <T> void setGeneratedIdToEntity(T instance, IdAttribute idAttribute, long key) throws NoSuchFieldException {
        Field idField = idAttribute.getField();
        Class<?> idType = idField.getType();

        for (IdAttributeResolver idAttributeResolver : ID_ATTRIBUTE_RESOLVERS) {
            if (idAttributeResolver.supports(idType)) {
                idAttributeResolver.setIdToEntity(instance, idField, key);
                return;
            }
        }
    }
}
