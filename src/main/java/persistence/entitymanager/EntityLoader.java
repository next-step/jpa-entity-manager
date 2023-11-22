package persistence.entitymanager;

import jdbc.JdbcTemplate;
import persistence.common.FieldClazz;
import persistence.common.FieldClazzList;
import persistence.sql.dml.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.Arrays;

public class EntityLoader {

    private final JdbcTemplate template;

    public EntityLoader(JdbcTemplate template) {
        this.template = template;
    }

    public <T> T load(Class<T> clazz, Long id) {
        String query = new SelectQueryBuilder().findById(clazz, Arrays.asList(id));
        FieldClazzList fieldClazzList = new FieldClazzList(clazz);
        return template.queryForObject(query, resultSet -> {
            try {
                if (!resultSet.next()) {
                    return null;
                }
                T entity = clazz.newInstance();
                for (FieldClazz fc : fieldClazzList.getFieldClazzList()) {
                    Object value = resultSet.getObject(fc.getName(), fc.getClazz());
                    fc.set(entity, value);
                }
                return entity;
            } catch (InstantiationException | IllegalAccessException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
