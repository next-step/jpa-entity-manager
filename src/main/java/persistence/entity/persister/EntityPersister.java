package persistence.entity.persister;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.entity.attribute.AttributeParser;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final AttributeParser attributeParser;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.attributeParser = new AttributeParser();
    }

    public <T> T insert(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass(), attributeParser);
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

    public <T> T findById(Class<T> clazz, String id) {
        EntityAttribute entityAttribute = EntityAttribute.of(clazz, attributeParser);
        IdAttribute idAttribute = attributeParser.parseIdAttribute(clazz);

        String sql = SelectQueryBuilder.of(entityAttribute).where(idAttribute.getColumnName(), id).prepareStatement();

        return jdbcTemplate.queryForObject(sql, rs -> mapResultSetToEntity(clazz, idAttribute, rs));
    }

    public <T> void remove(T entity, String id) {
        EntityAttribute entityAttribute = EntityAttribute.of(entity.getClass(), new AttributeParser());
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        String deleteDML = deleteQueryBuilder.prepareStatement(entityAttribute, id);
        jdbcTemplate.execute(deleteDML);
    }

    private <T> T mapResultSetToEntity(Class<T> clazz, IdAttribute idAttribute, ResultSet rs) {
        try {
            if (!rs.next()) {
                return null;
            }

            T instance = instantiateClass(clazz);
            setIdFromResultSet(instance, idAttribute, rs);
            setGeneralFieldsFromResultSet(instance, rs);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void setIdFromResultSet(T instance, IdAttribute idAttribute, ResultSet rs) throws Exception {
        Field idField = Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();

        idField.setAccessible(true);
        idField.set(instance, rs.getObject(idAttribute.getColumnName()));
    }

    private <T> void setGeneralFieldsFromResultSet(T instance, ResultSet rs) throws Exception {
        List<Field> generalFields = Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(it -> !it.isAnnotationPresent(Id.class) && it.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());

        for (Field field : generalFields) {
            field.setAccessible(true);
            GeneralAttribute generalAttribute = attributeParser.parseGeneralAttribute(field);
            Object value = rs.getObject(generalAttribute.getColumnName());
            field.set(instance, value);
        }
    }

    private <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("[%s] 클래스 초기화 실패", clazz.getSimpleName()), e);
        }
    }

    private <T> void setGeneratedIdToEntity(T instance, IdAttribute idAttribute, long key) throws NoSuchFieldException {
        String fieldName = idAttribute.getFieldName();
        Field idField = instance.getClass().getDeclaredField(fieldName);
        Class<?> keyType = idField.getType();

        if (keyType == Long.class) {
            setGeneratedIdToEntity(instance, idField, key);
        }
        if (keyType == Integer.class) {
            setGeneratedIdToEntity(instance, idField, (int) key);
        }
    }

    private <T, U> void setGeneratedIdToEntity(T instance, Field idField, U key) throws NoSuchFieldException {
        idField.setAccessible(true);
        try {
            idField.set(instance, key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("인스턴스의 ID 필드에 키 값을 할당하는데 실패했습니다.", e);
        }
    }
}
