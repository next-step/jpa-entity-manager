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

    public EntityPersister(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 조회한다.
    public <T> T find(Class<T> clazz, Long id) {
        EntityInfo<T> entityObject = EntityInfo.createEntityInfo(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        SelectByIdQueryBuilder queryBuilder = new SelectByIdQueryBuilder();
        Object findObject = jdbcTemplate.queryForObject(queryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(clazz, id)), resultSet -> EntityMapper.mapRow(resultSet, clazz));
        this.persistenceContext.insertEntity(EntityInfo.createEntityInfo(id, findObject.getClass()), findObject);
        return clazz.cast(findObject);
    }

    //데이터 리스트를 조회한다.
    public <T> List<T> findAll(Class<T> clazz) {
        SelectAllQueryBuilder queryBuilder = new SelectAllQueryBuilder();
        List<T> list = jdbcTemplate.query(queryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(clazz)), resultSet -> EntityMapper.mapRow(resultSet, clazz));
        for (T findObject : list) {
            confirmExistPersistContext(findObject, clazz);
        }
        return list;
    }

    //데이터를 반영한다.
    public void persist(Object entityInstance) {
        InsertQueryBuilder queryBuilder = new InsertQueryBuilder();
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(queryBuilder.buildQuery(dmlBuilderData));
        this.persistenceContext.insertEntity(EntityInfo.createEntityInfo(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    //데이터를 수정한다.
    public void update(Object entityInstance) {
        confirmEntityDataExist(entityInstance);
        UpdateQueryBuilder queryBuilder = new UpdateQueryBuilder();
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(queryBuilder.buildQuery(dmlBuilderData));
        this.persistenceContext.updateEntity(EntityInfo.createEntityInfo(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    //데이터를 제거한다.
    public void remove(Object entityInstance) {
        DeleteQueryBuilder queryBuilder = new DeleteQueryBuilder();
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        jdbcTemplate.execute(queryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)));
        this.persistenceContext.updateEntity(EntityInfo.createEntityInfo(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
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

    //영속성 컨텍스트에 Entity 가 이미 조회됐는지 여부를 확인하고 update 혹은 insert 해준다.
    private <T> void confirmExistPersistContext(T findObject, Class<T> clazz) {
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(findObject);
        EntityInfo<T> entityObject = EntityInfo.createEntityInfo(dmlBuilderData.getId(), clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);

        if (persistObject != null) {
            this.persistenceContext.updateEntity(entityObject, findObject);
        } else {
            this.persistenceContext.insertEntity(entityObject, findObject);
        }
    }

}
