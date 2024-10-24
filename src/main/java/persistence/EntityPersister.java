package persistence;

import builder.dml.DMLBuilderData;
import builder.dml.builder.*;
import jdbc.EntityMapper;
import jdbc.JdbcTemplate;

import java.util.List;

public class EntityPersister {

    private final static String DATA_NOT_EXIST_MESSAGE = "데이터가 존재하지 않습니다. : ";
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;

    private final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private final SelectAllQueryBuilder selectAllQueryBuilder = new SelectAllQueryBuilder();
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    public EntityPersister(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 조회한다.
    public <T> T find(Class<T> clazz, Long id) {
        EntityInfo<T> entityObject = new EntityInfo<>(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        Object findObject = jdbcTemplate.queryForObject(selectByIdQueryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(clazz, id)), resultSet -> EntityMapper.mapRow(resultSet, clazz));

        this.persistenceContext.insertEntity(new EntityInfo<>(id, findObject.getClass()), findObject);
        return clazz.cast(findObject);
    }

    //데이터를 반영한다.
    public void persist(Object entityInstance) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(dmlBuilderData));
        this.persistenceContext.insertEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    //데이터를 수정한다.
    public void merge(Object entityInstance) {
        confirmEntityDataExist(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(dmlBuilderData));
        this.persistenceContext.insertEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    //데이터를 제거한다.
    public void remove(Object entityInstance) {
        DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder();
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(queryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)));
        this.persistenceContext.deleteEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()));
    }

    //조회되는 데이터가 존재하는지 확인한다.
    private void confirmEntityDataExist(Object entityInstance) {
        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();
        try {
            jdbcTemplate.queryForObject(
                    queryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)),
                    resultSet -> EntityMapper.mapRow(resultSet, entityInstance.getClass())
            );
        } catch (RuntimeException e) {
            throw new RuntimeException(DATA_NOT_EXIST_MESSAGE + entityInstance.getClass().getSimpleName());
        }
    }
}
