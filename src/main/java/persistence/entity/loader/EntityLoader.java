package persistence.entity.loader;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.entity.attribute.AttributeParser;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.id.IdAttribute;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T load(Class<T> clazz, String id) {
        EntityAttribute entityAttribute = EntityAttribute.of(clazz);
        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        String sql = SelectQueryBuilder.of(entityAttribute).where(idAttribute.getColumnName(), id).prepareStatement();

        return jdbcTemplate.queryForObject(sql, rs -> mapResultSetToEntity(clazz, idAttribute, rs));
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

    private <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("[%s] 클래스 초기화 실패", clazz.getSimpleName()), e);
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
            GeneralAttribute generalAttribute = AttributeParser.parseGeneralAttribute(field);
            Object value = rs.getObject(generalAttribute.getColumnName());
            field.set(instance, value);
        }
    }
}
