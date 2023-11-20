package persistence.entitymanager;

import jdbc.JdbcTemplate;
import persistence.common.FieldClazz;
import persistence.common.FieldClazzList;
import persistence.sql.dml.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.Arrays;

public class SimpleEntityManager implements EntityManager {

    private final JdbcTemplate template;
    private final EntityPersister persister;

    public SimpleEntityManager(JdbcTemplate template) {
        this.template = template;
        this.persister = new EntityPersister(template);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        String query = new SelectQueryBuilder().findById(clazz, Arrays.asList(Id));
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

    @Override
    public Object persist(Object entity) {
        persister.insert(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persister.delete(entity);
    }
}
