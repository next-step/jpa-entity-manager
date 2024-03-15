package persistence.entity;

import dialect.Dialect;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import pojo.EntityMetaData;

public class EntityPersisterImpl implements EntityPersister {

    /**
     * 엔티티 클래스와 데이터베이스 테이블 간의 매핑 정보를 관리하고, 엔티티의 상태 변화를 데이터베이스에 반영하는 구체적인 작업을 수행
     * 예를 들어, EntityManager 를 통해 persist 메서드가 호출되면,
     * -> 내부적으로 Hibernate 는 해당 엔티티의 EntityPersister 를 사용하여 SQL INSERT 문을 생성하고 실행
     */

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaData entityMetaData;

    public EntityPersisterImpl(JdbcTemplate jdbcTemplate, EntityMetaData entityMetaData) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaData = entityMetaData;
    }

    @Override
    public Object insert(Object entity) {
        UpdateQueryBuilder queryBuilder = new UpdateQueryBuilder(entityMetaData);
        return jdbcTemplate.executeAndReturnKey(queryBuilder.insertQuery(entity));
    }

    @Override
    public boolean update(Object entity) {
        UpdateQueryBuilder queryBuilder = new UpdateQueryBuilder(entityMetaData);
        return jdbcTemplate.executeUpdate(queryBuilder.updateQuery(entity)) > 0;
    }

    @Override
    public void delete(Object entity) {
        DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder(entityMetaData);
        jdbcTemplate.execute(queryBuilder.deleteByIdQuery(entity));
    }
}
