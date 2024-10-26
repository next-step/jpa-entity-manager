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

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 반영한다.
    public void persist(DMLBuilderData dmlBuilderData) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(dmlBuilderData));
    }

    //데이터를 수정한다.
    public void merge(DMLBuilderData dmlBuilderData) {
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(dmlBuilderData));
    }

    //데이터를 제거한다.
    public void remove(DMLBuilderData dmlBuilderData) {
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(dmlBuilderData));
    }

}
