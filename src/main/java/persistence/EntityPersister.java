package persistence;

import builder.dml.DMLBuilderData;
import builder.dml.builder.DeleteQueryBuilder;
import builder.dml.builder.InsertQueryBuilder;
import builder.dml.builder.SelectByIdQueryBuilder;
import builder.dml.builder.UpdateQueryBuilder;
import jdbc.EntityMapper;
import jdbc.JdbcTemplate;

public class EntityPersister {

    private final static String DATA_NOT_EXIST_MESSAGE = "데이터가 존재하지 않습니다. : ";
    private final JdbcTemplate jdbcTemplate;

    private final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 조회한다.
    public <T> T find(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(selectByIdQueryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(clazz, id)), resultSet -> EntityMapper.mapRow(resultSet, clazz));
    }

    //데이터를 반영한다.
    public void persist(Object entityInstance) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)));
    }

    //데이터를 수정한다.
    public void merge(Object entityInstance) {
        confirmEntityDataExist(entityInstance);
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)));
    }

    //데이터를 제거한다.
    public void remove(Object entityInstance) {
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(DMLBuilderData.createDMLBuilderData(entityInstance)));
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
