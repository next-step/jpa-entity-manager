package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private static final List<IdTypeHandler> ID_TYPE_HANDLERS = Arrays.asList(
            new LongIdTypeHandler(),
            new IntegerIdTypeHandler()
    );
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

        for (IdTypeHandler handler : ID_TYPE_HANDLERS) {
            if (handler.support(idType)) {
                handler.setGeneratedIdToEntity(instance, idField, key);
                return;
            }
        }
    }
}
